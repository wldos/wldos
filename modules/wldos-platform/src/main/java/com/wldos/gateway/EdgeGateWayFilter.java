/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.gateway;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultJson;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import com.wldos.framework.support.auth.AccountNotActivatedException;
import io.github.wldos.framework.support.auth.JWTTool;
import io.github.wldos.framework.support.auth.AuthResourceNotMountedException;
import io.github.wldos.framework.support.auth.TokenForbiddenException;
import io.github.wldos.platform.support.auth.enums.AuthDenyReason;
import io.github.wldos.framework.support.auth.TokenInvalidException;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.enums.UserStatusEnum;
import com.wldos.platform.core.service.AuthService;
import com.wldos.platform.core.service.DomainService;
import com.wldos.platform.core.service.UserService;
import io.github.wldos.platform.support.auth.vo.AuthVerify;
import io.github.wldos.framework.support.auth.vo.JWT;
import io.github.wldos.platform.support.resource.vo.AuthInfo;
import io.github.wldos.framework.support.storage.utils.StoreUtils;
import io.github.wldos.framework.support.web.EdgeHandler;
import io.github.wldos.framework.support.web.FilterRequestWrapper;
import io.github.wldos.gateway.plugins.IPluginApiGateway;
import com.wldos.gateway.edge.EdgeIpPolicyAction;
import com.wldos.gateway.edge.EdgeIpPolicyMatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 轻量级边缘网关。
 *
 * @author Yuanxi Universe
 * @date 2021-04-18
 * @version V1.0
 */
@Slf4j
public class EdgeGateWayFilter implements Filter {

	private String proxyPrefix;

	protected String domainHeader;

	private DomainService domainService;

	private AuthService authService;

	private UserService userService;

	private ResultJson resJson;

	private String tokenHeader;

	private EdgeHandler edgeHandler;

	/** 插件API网关 */
	private IPluginApiGateway pluginApiGateway;

	/** jwt全家桶工具 */
	protected JWTTool jwtTool;

	/**
	 * 系统日志（运维事件）写入器；可空（裁剪部署不开 audit 内核也能跑）。
	 *
	 * <p>凡被网关拒绝的 {@link BaseException}（鉴权失败、token 无效、非法请求等）均写入
	 * {@code edge.ACCESS_DENIED}；{@link ClientAbortException} 仍跳过（连接中断非安全事件）。
	 */
	private ISystemLogger systemLogger;

	private EdgeGatewayIpRateLimiter ipRateLimiter;

	private EdgeAccessDeniedAggregator accessDeniedAggregator;

	private EdgeGatewayProbeMatcher probeMatcher;

	private EdgeGatewayHookBridge edgeHookBridge;

	private Environment filterEnvironment;

	private String adminEmail;

	/** 要放行的uri前缀 */
	private List<String> excludeUris;

	/** 必须登陆的uri前缀 */
	private List<String> verifyTokenUris;

	/** 静态资源uri前缀 */
	private String staticUri;

	/** 记录操作日志uri前缀 */
	private List<String> recLogUris;

	public EdgeGateWayFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) {
		ServletContext ctx = filterConfig.getServletContext();
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(ctx);
		if (ac == null)	return;

		Environment env = ac.getEnvironment();

		String pathIgnore = env.getProperty("gateway.ignore.path");
		String tokenUri = env.getProperty("gateway.token.path");
		String logUri = env.getProperty("gateway.log.path");
		this.proxyPrefix = env.getProperty("gateway.proxy.prefix");
		this.domainHeader = Constants.WLDOS_DOMAIN_HEADER;
		this.tokenHeader = Constants.TOKEN_ACCESS_HEADER;

		assert pathIgnore != null;
		this.excludeUris = Arrays.asList(pathIgnore.split(","));
		assert tokenUri != null;
		this.verifyTokenUris = Arrays.asList(tokenUri.split(","));
		this.staticUri = "/" + StoreUtils.PUBLIC_AREA;
		assert logUri != null;
		this.recLogUris = Arrays.asList(logUri.split(","));
		this.domainService = ac.getBean(DomainService.class);
		this.authService = ac.getBean(AuthService.class);
		this.userService = ac.getBean(UserService.class);
		this.edgeHandler = ac.getBean(EdgeHandler.class);
		this.pluginApiGateway = ac.getBean(IPluginApiGateway.class);
		this.jwtTool = ac.getBean(JWTTool.class);
		this.resJson = ac.getBean(ResultJson.class);
		// systemLogger 可选：未启用 audit 内核时为 null，throwException 内已做空判
		try {
			this.systemLogger = ac.getBean(ISystemLogger.class);
		}
		catch (Exception ignored) {
			this.systemLogger = null;
		}
		try {
			this.ipRateLimiter = ac.getBean(EdgeGatewayIpRateLimiter.class);
		}
		catch (Exception ignored) {
			this.ipRateLimiter = null;
		}
		try {
			this.accessDeniedAggregator = ac.getBean(EdgeAccessDeniedAggregator.class);
		}
		catch (Exception ignored) {
			this.accessDeniedAggregator = null;
		}
		try {
			this.probeMatcher = ac.getBean(EdgeGatewayProbeMatcher.class);
		}
		catch (Exception ignored) {
			this.probeMatcher = null;
		}
		try {
			this.edgeHookBridge = ac.getBean(EdgeGatewayHookBridge.class);
		}
		catch (Exception ignored) {
			this.edgeHookBridge = null;
		}
		this.filterEnvironment = env;
		this.adminEmail = env.getProperty("wldos_platform_adminEmail", "306991142#qq.com");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		String reqUri = null;
		String userIP = null;
		JWT jwt = null;
		HttpServletResponse response = null;
		HttpServletRequest request = null;
		try {
			response = (HttpServletResponse) res;
			this.edgeHandler.handleResponse(response);
			request = (HttpServletRequest) req;

			reqUri = request.getRequestURI();
			userIP = GatewayClientIpResolver.resolve(request);

			// Mesh internal API 走 MeshInternalAuthFilter，不经 JWT 网关
			if (reqUri.startsWith("/internal/mesh")) {
				chain.doFilter(request, res);
				return;
			}

			boolean isPrefix = false;
			if (reqUri.startsWith(this.proxyPrefix)) {
				isPrefix = true;
				// 替换掉前缀
				reqUri = reqUri.substring(this.proxyPrefix.length());
			}

			if (reqUri.startsWith(this.staticUri)) {
				chain.doFilter(request, res);
				return;
			}

			EdgeIpPolicyMatch ipRuleMatch = resolveIpPolicyMatch(userIP);
			if (ipRuleMatch != null && ipRuleMatch.getAction() == EdgeIpPolicyAction.DENY) {
				writeIpDenyResponse(response);
				return;
			}

			if (dropProbeRequest(request, reqUri, response, userIP)) {
				return;
			}

			// 检查是否为插件API请求
			String pluginCode = this.pluginApiGateway.isPluginApiRequest(reqUri);
			if (pluginCode != null) {
				// 处理插件API请求
				handlePluginApiRequest(request, response, reqUri, pluginCode, userIP);
				return;
			}			

			String domain = this.edgeHandler.getDomain(request, this.domainHeader);
			boolean isExcludeUri = GateWayHelper.isMatchUri(reqUri, this.excludeUris);
			WoDomain reqDomain = this.domainService.queryDomainByName(domain, isExcludeUri);
			if (reqDomain == null) {// @todo 当没有设置域名 或者 没有开启多域名时，应存在默认域名
				log.error("使用了非法域名：{}", domain);
				throw new IllegalDomainException("使用了非法域名，禁止访问！" + domain);
			}

			String token = request.getHeader(this.tokenHeader);

			if (isExcludeUri) {
				Long domainId = reqDomain.getId();
				jwt = jwtTool.popJwt(token, userIP, reqUri, reqDomain.getSiteDomain(), domainId);
				if (jwt == null) {
					jwt = jwtTool.popJwt("", userIP, reqUri, reqDomain.getSiteDomain(), domainId);
				}
				Long contextDomainId = resolveRequestContextDomainId(reqDomain, jwt);
				FilterRequestWrapper headers = this.edgeHandler.handleRequest(request, jwt, contextDomainId, this.proxyPrefix);
				chain.doFilter(headers, res);
				return;
			}

			if (ObjectUtils.isBlank(token)) {
				if (!tryAcquireNoTokenRateLimit(userIP, strictRateMatch(ipRuleMatch))) {
					writeRateLimitResponse(response);
					return;
				}
				throw new TokenInvalidException("token is blank");
			}

			Long domainId = reqDomain.getId();
			jwt = jwtTool.popJwt(token, userIP, reqUri, reqDomain.getSiteDomain(), domainId);

			if (this.jwtTool.isExpired(jwt)) { // 验证token是否过期
				throw new TokenInvalidException("user token is expired");
			}

			if (GateWayHelper.isMatchUri(reqUri, this.verifyTokenUris)) {
				if (this.authService.isGuest(jwt.getUserId()))
					throw new TokenInvalidException("user token is invalid");
			}

			if (!isPrefix) {
				throw new BaseException("非法请求(invalid request)!");
			}

			Long contextDomainId = resolveRequestContextDomainId(reqDomain, jwt);

			String appCode = this.authService.resolveAuthAppCode(contextDomainId, reqUri, request.getMethod());
			if (appCode == null) {
				throw new BaseException("root path forbidden!");
			}

			Long authComId = this.userService.resolvePortalAuthComId(jwt.getUserId(), contextDomainId, jwt.getWorkComId());
			if (authComId == null) {
				authComId = jwt.getWorkComId();
			}
			AuthVerify authVerify = this.authService.verifyReqAuth(contextDomainId, appCode, jwt.getUserId(), authComId, reqUri, request.getMethod());

			if (authVerify.isAuth()) {
				AuthInfo auth = authVerify.getAuthInfo();
				if (GateWayHelper.isMatchUri(reqUri, this.recLogUris) && auth != null) {
					log.info("{}, {}, {}, {}, {}, {}", userIP, jwt.getUserId(), auth.getResourceName(), auth.getResourcePath()
							, request.getQueryString(), request.getParameterMap().keySet());
				}
			}
			else {
				if (this.authService.isGuest(jwt.getUserId())) {
					throw new TokenInvalidException("Forbidden, guest no auth!");
				}
				WoUser woUser = this.userService.findById(jwt.getUserId());
				if (woUser != null && UserStatusEnum.notActive.getValue().equals(woUser.getStatus())) {
					throw new AccountNotActivatedException(
							"您的账号尚未激活，请先完成邮箱激活后再使用个人中心等功能。若未收到邮件，可在登录页尝试重新发送或联系管理员。");
				}
				if (authVerify.getDenyReason() == AuthDenyReason.NOT_MOUNTED) {
					throw new AuthResourceNotMountedException("本站未开放该资源");
				}
				throw new TokenForbiddenException("Forbidden,no auth!");
			}

			FilterRequestWrapper headers = this.edgeHandler.handleRequest(request, jwt, contextDomainId, this.proxyPrefix);
			chain.doFilter(headers, res);
		}
		catch (Exception e) {
			String userId = jwt == null || jwt.getUserId() == null ? "" : jwt.getUserId().toString();
			if (e instanceof BaseException)
				this.throwException(response, (BaseException) e, userIP, reqUri, userId);
			else if (e instanceof ClientAbortException) {
				log.warn("主机中止了一个连接");
			}
			else {
				this.throwException(response, new BaseException("请求异常，请重试！"), userIP, reqUri, userId);
				e.printStackTrace();
			}
			// 网关拒绝/异常入系统日志（ClientAbort 除外）
			recordEdgeRejection(e, request, userIP, reqUri, userId);
		}
	}

	private boolean dropProbeRequest(HttpServletRequest request, String reqUri, HttpServletResponse response,
			String userIP) {
		if (this.probeMatcher == null || !this.probeMatcher.matches(reqUri)) {
			return false;
		}
		writeProbeDropResponse(response);
		recordProbeHit(request, reqUri, userIP);
		return true;
	}

	private void writeProbeDropResponse(HttpServletResponse response) {
		if (response == null) {
			return;
		}
		try {
			response.setStatus(404);
			response.setContentLength(0);
		}
		catch (Exception e) {
			log.debug("探针路径短响应失败：{}", e.getClass().getSimpleName());
		}
	}

	private boolean tryAcquireNoTokenRateLimit(String userIP) {
		return tryAcquireNoTokenRateLimit(userIP, null);
	}

	private boolean tryAcquireNoTokenRateLimit(String userIP, EdgeIpPolicyMatch rateOverride) {
		if (this.ipRateLimiter == null) {
			return true;
		}
		return this.ipRateLimiter.tryAcquireNoToken(userIP, rateOverride);
	}

	private void writeRateLimitResponse(HttpServletResponse response) {
		if (response == null) {
			return;
		}
		try {
			response.setStatus(429);
			boolean minimal = this.ipRateLimiter == null || this.ipRateLimiter.minimalBody();
			if (minimal) {
				response.setContentLength(0);
				return;
			}
			response.setContentType("text/plain;charset=utf-8");
			response.getOutputStream().write("Too Many Requests\n".getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			log.error("429 响应写入失败：{}", e.getClass().getName());
		}
	}

	/**
	 * 网关拒绝与未捕获异常写入系统日志（运维侧可见）。
	 *
	 * <ul>
	 *   <li>{@link IllegalDomainException} → {@code edge.ILLEGAL_DOMAIN} (WARN)；</li>
	 *   <li>其他 {@link BaseException}（token 无效/无权限/非法请求等）→ {@code edge.ACCESS_DENIED} (WARN)；</li>
	 *   <li>{@link ClientAbortException} → 不写；</li>
	 *   <li>其他未捕获 {@link Exception} → {@code edge.GATEWAY_500} (ERROR)。</li>
	 * </ul>
	 */
	private void recordEdgeRejection(Exception e, HttpServletRequest request, String userIP, String reqUri,
			String userId) {
		if (this.systemLogger == null || e == null) {
			return;
		}
		try {
			if (e instanceof ClientAbortException) {
				return;
			}
			SystemEvent ev;
			if (e instanceof IllegalDomainException) {
				ev = SystemEvent.warn("edge", "ILLEGAL_DOMAIN",
						"非法域名访问被拒: " + ObjectUtils.string(e.getMessage()));
			}
			else if (e instanceof BaseException) {
				ev = SystemEvent.warn("edge", "ACCESS_DENIED",
						"网关拒绝访问: " + ObjectUtils.string(e.getMessage()));
			}
			else {
				ev = SystemEvent.error("edge", "GATEWAY_500",
						"网关未捕获异常: " + e.getClass().getSimpleName() + " - "
								+ ObjectUtils.string(e.getMessage()));
			}
			EdgeRejectionContext ctx = EdgeRejectionContext.of(request, userIP, reqUri, userId);
			// 分类（含路由真值查询等有计算量的判断）不在这里同步做：交给 accessDeniedAggregator，
			// 聚合模式下等窗口 flush 时才算一次，不占用当前请求处理线程。
			if (this.accessDeniedAggregator != null) {
				this.accessDeniedAggregator.submit(this.systemLogger, ev, ctx);
			}
			else {
				this.systemLogger.recordAsync(ev);
			}
		}
		catch (Exception ignored) {
			// 审计旁路必须吞异常，绝不让自己反过来影响主请求链路
		}
	}

	private void throwException(HttpServletResponse response, BaseException ex, String userIP, String reqUri, String userId) {

		try {
			int code = ex.getCode();
			String message = code == 500 ? "Sorry, the server is abnormal, please try again, or contact the administrator: " + this.adminEmail
					: ObjectUtils.string(ex.getMessage());
			response.setStatus(200); // HTTP状态码始终为200
			response.setContentType("application/json;charset=utf-8");
			if (ex instanceof TokenInvalidException) {
				// token 缺失/过期/无效属正常会话生命周期，降级为 info，避免刷屏误判为系统故障
				log.info("userIP: {}, reqUri: {}, userId: {}, token invalid: {}", userIP, reqUri, userId, message);
			}
			else {
				log.error("userIP: {}, reqUri: {}, userId: {}, exception: {}", userIP, reqUri, userId, message);
			}
		
			Result result = new Result(code, message);
			String json = this.resJson.ok(result);
			response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			log.error("异常处理时异常：{}", e.getClass().getName());
		}
	}

	/**
	 * 处理插件API请求
	 * 
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @param reqUri 请求URI
	 * @param pluginCode 插件代码
	 * @param userIP 用户IP
	 */
	private void handlePluginApiRequest(HttpServletRequest request, HttpServletResponse response, 
	                                  String reqUri, String pluginCode, String userIP) {
		try {
			// 获取域名信息
			String domain = this.edgeHandler.getDomain(request, this.domainHeader);
			WoDomain reqDomain = this.domainService.queryDomainByName(domain, false);
			if (reqDomain == null) {
				log.error("插件API请求使用了非法域名：{}", domain);
				throw new IllegalDomainException("使用了非法域名，禁止访问！" + domain);
			}

			// 获取JWT令牌
			String token = request.getHeader(this.tokenHeader);
			if (ObjectUtils.isBlank(token)) {
				EdgeIpPolicyMatch pluginIpMatch = resolveIpPolicyMatch(userIP);
				if (!tryAcquireNoTokenRateLimit(userIP, strictRateMatch(pluginIpMatch))) {
					writeRateLimitResponse(response);
					return;
				}
				throw new TokenInvalidException("token is blank");
			}
			JWT jwt = jwtTool.popJwt(token, userIP, reqUri, reqDomain.getSiteDomain(), reqDomain.getId());
			if (this.jwtTool.isExpired(jwt)) {
				throw new TokenInvalidException("user token is expired");
			}

			// 验证插件API权限
			if (!this.pluginApiGateway.verifyPluginApiAuth(request, pluginCode, jwt, reqDomain.getId())) {
				throw new TokenForbiddenException("Forbidden, no plugin API auth!");
			}

			// 转发到插件Controller
			boolean forwarded = this.pluginApiGateway.forwardPluginApi(request, response, pluginCode, jwt, reqDomain.getId());
			if (!forwarded) {
				throw new BaseException("插件API转发失败！");
			}

		} catch (Exception e) {
			String userId = "";
			try {
				JWT jwt = jwtTool.popJwt(request.getHeader(this.tokenHeader), userIP, reqUri, "", 0L);
				userId = jwt != null ? jwt.getUserId().toString() : "";
			} catch (Exception ignored) {
				// 忽略JWT解析错误
			}

			if (e instanceof BaseException) {
				this.throwException(response, (BaseException) e, userIP, reqUri, userId);
			} else {
				this.throwException(response, new BaseException("插件API请求异常，请重试！"), userIP, reqUri, userId);
				log.error("插件API请求处理异常: {}", e.getMessage(), e);
			}
			recordEdgeRejection(e, request, userIP, reqUri, userId);
		}
	}

	private EdgeIpPolicyMatch resolveIpPolicyMatch(String userIP) {
		if (this.edgeHookBridge == null) {
			return null;
		}
		return this.edgeHookBridge.resolveIpPolicy(userIP);
	}

	private static EdgeIpPolicyMatch strictRateMatch(EdgeIpPolicyMatch match) {
		if (match != null && match.getAction() == EdgeIpPolicyAction.RATE_STRICT) {
			return match;
		}
		return null;
	}

	private void writeIpDenyResponse(HttpServletResponse response) {
		if (response == null) {
			return;
		}
		try {
			int status = 404;
			if (this.filterEnvironment != null) {
				status = this.filterEnvironment.getProperty("wldos.gateway.ip-deny.response-status", Integer.class, 404);
			}
			response.setStatus(status);
			response.setContentLength(0);
		}
		catch (Exception e) {
			log.debug("IP 拒绝响应失败：{}", e.getClass().getSimpleName());
		}
	}

	private void recordProbeHit(HttpServletRequest request, String reqUri, String userIP) {
		if (this.probeMatcher == null || this.systemLogger == null) {
			return;
		}
		try {
			EdgeRejectionContext ctx = EdgeRejectionContext.of(request, userIP, reqUri, "");
			if (this.edgeHookBridge == null || !this.edgeHookBridge.shouldAuditProbeHit(ctx, reqUri)) {
				return;
			}
			SystemEvent ev = SystemEvent.warn("edge", "PROBE_HIT", "探针路径命中: " + reqUri);
			// 分类同样挪到 accessDeniedAggregator 里做，不在这里同步算。
			if (this.accessDeniedAggregator != null) {
				this.accessDeniedAggregator.submit(this.systemLogger, ev, ctx);
			}
			else {
				this.systemLogger.recordAsync(ev);
			}
		}
		catch (Exception ignored) {
			// 审计旁路
		}
	}

	/**
	 * 门户 Host 登录但 JWT 域为租户主站（体验沙箱等）时，业务上下文与鉴权挂载对齐 token 域。
	 * <p>
	 * 常规登录 JWT.domainId 与 Host 域一致，直接返回，无额外开销。
	 * 跨 Host 时不在网关重复查库：domainId/workComId 配对已在 {@code LoginHostAdmissionPolicy} 签发 token 时校验。
	 */
	private Long resolveRequestContextDomainId(WoDomain requestDomain, JWT jwt) {
		if (requestDomain == null) {
			return null;
		}
		if (jwt == null) {
			return requestDomain.getId();
		}
		Long tokenDomainId = jwt.getDomainId();
		if (tokenDomainId == null || tokenDomainId <= 0 || tokenDomainId.equals(requestDomain.getId())) {
			return requestDomain.getId();
		}
		Long workComId = jwt.getWorkComId();
		if (workComId != null && workComId > 0) {
			return tokenDomainId;
		}
		return requestDomain.getId();
	}

	@Override
	public void destroy() {
		log.debug("filter is destroy.");
	}

}