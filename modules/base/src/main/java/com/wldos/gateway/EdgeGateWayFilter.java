/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.gateway;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.common.Constants;
import com.wldos.common.res.Result;
import com.wldos.common.res.ResultJson;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.domain.DomainUtils;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.auth.JWTTool;
import com.wldos.common.exception.BaseException;
import com.wldos.support.auth.TokenForbiddenException;
import com.wldos.support.auth.TokenInvalidException;
import com.wldos.sys.base.entity.WoDomain;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.base.service.DomainService;
import com.wldos.support.storage.utils.StoreUtils;
import com.wldos.sys.base.vo.AuthInfo;
import com.wldos.sys.base.vo.AuthVerify;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 微服务网关、请求鉴权，反向代理，服务发现和限流等操作客户端，支持ServiceMesh架构。
 * 本网关支持作认证中心部署，但建议只用于微服务处理外部请求的代理，另外用独立的网关服务做分布式系统的认证中心。
 * 如果你打算尝试ServiceMesh架构，可以使用本网关实现服务代理。
 * 如果你打算区分服务，可以定义两个以上filter分别拦截不同的URLPatterns。
 *
 * @author 树悉猿
 * @date 2021-04-18
 * @version V1.0
 */
@Slf4j
public class EdgeGateWayFilter implements Filter {

	private String proxyPrefix;

	protected String domainHeader;

	private DomainService domainService;

	private AuthService authService;

	private ResultJson resJson;

	private String tokenHeader;

	/** jwt全家桶工具 */
	protected JWTTool jwtTool;
	/** 要放行的uri前缀 */
	private List<String> excludeUris;
	/** 必须登陆的uri前缀 */
	private List<String> verifyTokenUris;
	/** 静态资源uri前缀 */
	private String staticUri;
	/** 记录操作日志uri前缀 */
	private List<String> recLogUris;

	@Override
	public void init(FilterConfig filterConfig) {
		ServletContext ctx = filterConfig.getServletContext();
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(ctx);
		assert ac != null;
		Environment env = ac.getEnvironment();

		String pathIgnore = env.getProperty("gateway.ignore.path");
		String tokenUri = env.getProperty("gateway.token.path");
		String logUri = env.getProperty("gateway.log.path");
		this.proxyPrefix = env.getProperty("gateway.proxy.prefix");
		this.domainHeader = env.getProperty("wldos.domain.header");
		this.tokenHeader = env.getProperty("token.access.header");

		assert pathIgnore != null;
		this.excludeUris = Arrays.asList(pathIgnore.split(","));
		assert tokenUri != null;
		this.verifyTokenUris = Arrays.asList(tokenUri.split(","));
		this.staticUri = StoreUtils.PUBLIC_AREA;
		assert logUri != null;
		this.recLogUris = Arrays.asList(logUri.split(","));
		this.domainService = ac.getBean(DomainService.class);
		this.authService = ac.getBean(AuthService.class);
		this.jwtTool = ac.getBean(JWTTool.class);
		this.resJson = ac.getBean(ResultJson.class);
		log.info("网关启动成功");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		String reqUri = null;
		String userIP = null;
		JWT jwt = null;
		HttpServletResponse response = null;
		try {
			response = (HttpServletResponse) res;
			response.setHeader(HttpHeaders.SERVER, "wldos");
			HttpServletRequest request = (HttpServletRequest) req;

			reqUri = request.getRequestURI();
			userIP = IpUtils.getClientIp(request);

			// 1.拆前缀
			boolean isPrefix = false;
			if (reqUri.startsWith(this.proxyPrefix)) {
				isPrefix = true;
				// 替换掉前缀
				reqUri = reqUri.substring(this.proxyPrefix.length());
			}

			// 2.放过免签请求
			// 2.1静态资源直接放行
			if (reqUri.startsWith(this.staticUri)) {
				chain.doFilter(request, res);
				return;
			}

			// 先验证域，域必须合法，这里用header的用意在于支持移动端的多域场景（特别说物联网），另外这也是一种白名单管理方式
			String domain = DomainUtils.getDomain(request, this.domainHeader);
			WoDomain reqDomain = this.domainService.queryDomainByName(domain);
			if (reqDomain == null) {// @todo 当没有设置域名 或者 没有开启多域名时，应存在默认域名
				log.error("使用了非法域名: {}", domain);
				throw new IllegalDomainException("使用了非法域名，禁止访问！");
			}

			// token认证验签开始
			String token = request.getHeader(this.tokenHeader);
			Long domainId = reqDomain.getId();
			jwt = jwtTool.popJwt(token, userIP, reqUri, reqDomain.getSiteDomain(), domainId);

			// 2.2免签请求处理请求
			if (this.isMatchUri(reqUri, this.excludeUris)) {
				// api请求需要设置请求参数
				FilterRequestWrapper headers = this.handleRequest(request, jwt, domainId);
				chain.doFilter(headers, res);
				return;
			}

			if (ObjectUtils.isBlank(token)) { // 非免签路由，token不能为空（对当前请求路由，如果设置了游客权限或者没有设置任何权限，则游客有权限，请求必须携带游客token，如果游客无权限，必须是有效token）
				throw new TokenInvalidException("token is blank");
			}

			if (this.jwtTool.isExpired(jwt)) { // 验证token是否过期
				throw new TokenInvalidException("user token is expired");
			}

			// 3.token验证路径验证是否登陆
			if (this.isMatchUri(reqUri, this.verifyTokenUris)) {
				if (this.authService.isGuest(jwt.getUserId())) // 游客必须登陆
					throw new TokenInvalidException("user token is invalid");
			}

			if (!isPrefix) { // 1、2、3都过，还不带前缀，一律视为非法请求
				throw new BaseException("非法请求(invalid request)!");
			}

			String appCode = this.appCode(reqUri);
			if (appCode == null) {
				throw new BaseException("root path forbidden!");
			}
			// 认证通过说明已登录，鉴权开始 （授权在登录模块）
			AuthVerify authVerify = this.authService.verifyReqAuth(reqDomain.getId(), appCode, jwt.getUserId(), jwt.getTenantId(), reqUri, request.getMethod());
			// 仲裁鉴权结果，存在放行，否则终止并警告
			if (authVerify.isAuth()) {
				AuthInfo auth = authVerify.getAuthInfo();
				if (this.isMatchUri(reqUri, this.recLogUris) && auth != null) { // 定制日志审计模块：资源存在记录日志，后期记入数据库，与免签路由配合只记录重要功能，游客可访问路由放行，前端行为使用前端技术
					log.info("{}, {}, {}, {}, {}, {}", IpUtils.getClientIp(request), jwt.getUserId(), auth.getResourceName(), auth.getResourcePath()
							, request.getQueryString(), request.getParameterMap().keySet());
				}
			}
			else {
				if (this.authService.isGuest(jwt.getUserId())) {
					throw new TokenInvalidException("Forbidden, guest no auth!");
				}
				throw new TokenForbiddenException("Forbidden,no auth!");
			}
			// api请求需要设置请求参数
			FilterRequestWrapper headers = this.handleRequest(request, jwt, domainId);

			// 鉴权通过，放行！
			chain.doFilter(headers, res);
		} catch (Exception e) {
			String userId = jwt == null ? "" : jwt.getUserId().toString();
			if (e instanceof BaseException)
				this.throwException(response, (BaseException) e, userIP, reqUri, userId);
			else if (e instanceof ClientAbortException) {
				log.info("主机中止了一个连接");
			}
			else {
				this.throwException(response, new BaseException("请求异常，请重试！"), userIP, reqUri, userId);
				e.printStackTrace();
			}
		}
	}

	private FilterRequestWrapper handleRequest(HttpServletRequest request, JWT jwt, Long domainId) {
		FilterRequestWrapper headers = new FilterRequestWrapper(request);
		headers.setUriPrefix(this.proxyPrefix);

		headers.add(Constants.CONTEXT_KEY_USER_ID, ObjectUtils.string(jwt.getUserId()));
		headers.add(Constants.CONTEXT_KEY_USER_TENANT, ObjectUtils.string(jwt.getTenantId()));
		headers.add(Constants.CONTEXT_KEY_USER_DOMAIN, ObjectUtils.string(domainId));
		Date d = jwt.getExpireDate();
		headers.add(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME, d == null ? "0" : String.valueOf(jwt.getExpireDate().getTime()));

		return headers;
	}

	/**
	 * 验证当前uri的前缀是否匹配目标路由前缀
	 *
	 * @param reqUri 请求uri
	 * @return 是否
	 */
	private boolean isMatchUri(String reqUri, List<String> target) {
		for (String s : target) {
			if (reqUri.startsWith(s))
				return true;
		}
		return false;
	}

	/**
	 * 应用编码最长5位，作为应用在平台上的请求路径前缀。@todo 应该为appId，从缓存获取appId，或者其他的方式优化权限验证的性能，权限逻辑：资源 ~ 应用 ~ 域，资源 ~ 权限 ~ 角色 ~ 组织 ~ 租户|用户
	 *
	 * @param reqUri 请求资源URI
	 * @return 应用编码
	 */
	private String appCode(String reqUri) {
		try {
			String root = "/";
			if ("".equals(reqUri) || root.equals(ObjectUtils.string(reqUri).trim()))
				return root;

			String[] temp = reqUri.split(root);

			return ObjectUtils.isBlank(temp[1]) ? null : temp[1].toLowerCase();
		} catch (Exception e) {
			log.error("请求截取应用编码异常");
		}
		return null;
	}
	// 异常处理，与前端交互
	private void throwException(HttpServletResponse response, BaseException ex, String userIP, String reqUri, String userId) {

		try {
			int status = ex.getStatus();
			String message = status == 500 ? "Sorry, the server is abnormal, please try again, or contact the administrator: support#zhiletu.com"
					: ObjectUtils.string(ex.getMessage());
			response.setStatus(status == 500 ? 200 : status);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			log.error("userIP: {}, reqUri: {}, userId: {}, exception: {}", userIP, reqUri, userId, message);
			String json = this.resJson.ok(new Result(status, message));
			response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			log.error("异常处理时异常：{}", e.getClass().getName());
		}
	}

	@Override
	public void destroy() {
		log.debug("filter is destroy.");
	}
}
