/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
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
import com.wldos.common.utils.domain.DomainUtils;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.auth.TokenForbiddenException;
import com.wldos.support.auth.TokenInvalidException;
import com.wldos.support.auth.vo.AuthVerify;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.resource.vo.AuthInfo;
import com.wldos.support.storage.utils.StoreUtils;
import com.wldos.support.web.EdgeHandler;
import com.wldos.support.web.FilterRequestWrapper;
import com.wldos.sys.base.entity.WoDomain;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.base.service.DomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 边缘网关。
 *
 * @author 树悉猿
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
		if (ac == null)
			return;

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

			String domain = DomainUtils.getDomain(request, this.domainHeader);
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
				log.info("主机中止了一个连接");
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