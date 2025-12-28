/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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

import com.wldos.common.Constants;
import com.wldos.common.exception.BaseException;
import com.wldos.common.res.Result;
import com.wldos.common.res.ResultJson;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.framework.support.auth.JWTTool;
import com.wldos.framework.support.auth.TokenForbiddenException;
import com.wldos.framework.support.auth.TokenInvalidException;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.service.AuthService;
import com.wldos.platform.core.service.DomainService;
import com.wldos.platform.support.auth.vo.AuthVerify;
import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.platform.support.resource.vo.AuthInfo;
import com.wldos.framework.support.storage.utils.StoreUtils;
import com.wldos.framework.support.web.EdgeHandler;
import com.wldos.framework.support.web.FilterRequestWrapper;
import com.wldos.gateway.plugins.IPluginApiGateway;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 轻量级边缘网关。
 *
 * @author 元悉宇宙
 * @date 2021-04-18
 * @version V1.0
 */
@Slf4j
@RefreshScope
public class EdgeGateWayFilter implements Filter {

	private String proxyPrefix;

	protected String domainHeader;

	private DomainService domainService;

	private AuthService authService;

	private ResultJson resJson;

	private String tokenHeader;

	private EdgeHandler edgeHandler;

	/** 插件API网关 */
	private IPluginApiGateway pluginApiGateway;

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
		this.edgeHandler = ac.getBean(EdgeHandler.class);
		this.pluginApiGateway = ac.getBean(IPluginApiGateway.class);
		this.jwtTool = ac.getBean(JWTTool.class);
		this.resJson = ac.getBean(ResultJson.class);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		String reqUri = null;
		String userIP = null;
		JWT jwt = null;
		HttpServletResponse response = null;
		try {
			response = (HttpServletResponse) res;
			this.edgeHandler.handleResponse(response);
			HttpServletRequest request = (HttpServletRequest) req;

			reqUri = request.getRequestURI();
			userIP = IpUtils.getClientIp(request);

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
			Long domainId = reqDomain.getId();
			jwt = jwtTool.popJwt(token, userIP, reqUri, reqDomain.getSiteDomain(), domainId);

			if (isExcludeUri) {
				FilterRequestWrapper headers = this.edgeHandler.handleRequest(request, jwt, domainId, this.proxyPrefix);
				chain.doFilter(headers, res);
				return;
			}

			if (ObjectUtils.isBlank(token)) {
				throw new TokenInvalidException("token is blank");
			}

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

			String appCode = GateWayHelper.appCode(reqUri);
			if (appCode == null) {
				throw new BaseException("root path forbidden!");
			}

			AuthVerify authVerify = this.authService.verifyReqAuth(reqDomain.getId(), appCode, jwt.getUserId(), jwt.getTenantId(), reqUri, request.getMethod());

			if (authVerify.isAuth()) {
				AuthInfo auth = authVerify.getAuthInfo();
				if (GateWayHelper.isMatchUri(reqUri, this.recLogUris) && auth != null) {
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

			FilterRequestWrapper headers = this.edgeHandler.handleRequest(request, jwt, domainId, this.proxyPrefix);
			chain.doFilter(headers, res);
		}
		catch (Exception e) {
			String userId = jwt == null ? "" : jwt.getUserId().toString();
			if (e instanceof BaseException)
				this.throwException(response, (BaseException) e, userIP, reqUri, userId);
			else if (e instanceof ClientAbortException) {
				log.warn("主机中止了一个连接");
			}
			else {
				this.throwException(response, new BaseException("请求异常，请重试！"), userIP, reqUri, userId);
				e.printStackTrace();
			}
		}
	}

	@Value("${wldos_platform_adminEmail:306991142#qq.com}")
	private String adminEmail;

	private void throwException(HttpServletResponse response, BaseException ex, String userIP, String reqUri, String userId) {

		try {
			int status = ex.getStatus();
			String message = status == 500 ? "Sorry, the server is abnormal, please try again, or contact the administrator: " + this.adminEmail
					: ObjectUtils.string(ex.getMessage());
			response.setStatus(200); // HTTP状态码始终为200
			response.setContentType("application/json;charset=utf-8");
			log.error("userIP: {}, reqUri: {}, userId: {}, exception: {}", userIP, reqUri, userId, message);
			String json = this.resJson.ok(new Result(status, message));
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
				throw new TokenInvalidException("token is blank");
			}

			// 验证JWT令牌
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
		}
	}


	@Override
	public void destroy() {
		log.debug("filter is destroy.");
	}
}