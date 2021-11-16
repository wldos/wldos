/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.gateway;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.Constants;
import com.wldos.support.controller.web.Result;
import com.wldos.support.controller.web.ResultJson;
import com.wldos.support.util.IpUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.domain.DomainUtil;
import com.wldos.system.auth.JWT;
import com.wldos.system.auth.JWTTool;
import com.wldos.system.auth.exception.AuthException;
import com.wldos.system.auth.exception.TokenForbiddenException;
import com.wldos.system.auth.exception.UserTokenAuthException;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.exception.IllegalDomainException;
import com.wldos.system.core.service.AuthService;
import com.wldos.system.core.service.DomainService;
import com.wldos.system.storage.util.StoreUtils;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.vo.AuthVerify;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 微服务网关、请求鉴权，反向代理，服务发现和限流等操作客户端。
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
	@SuppressWarnings({"rawtypes"})
	private ResultJson resJson;

	private String tokenHeader;
	
	protected JWTTool jwtTool;
	
	private List<String> excludeUris;
	
	private List<String> verifyTokenUris;
	
	private String staticUri;
	
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
		try {
			HttpServletRequest request = (HttpServletRequest) req;

			reqUri = request.getRequestURI();
			userIP = IpUtils.getClientIp(request);

			boolean isPrefix = false;
			if (reqUri.startsWith(this.proxyPrefix)) {
				isPrefix = true;

				reqUri = reqUri.substring(this.proxyPrefix.length());
			}

			if (reqUri.startsWith(this.staticUri)) {
				chain.doFilter(request, res);
				return;
			}

			String token = request.getHeader(this.tokenHeader);
			jwt = jwtTool.popJwt(token, userIP, reqUri);

			if (this.isMatchUri(reqUri, this.excludeUris)) {

				FilterRequestWrapper headers = this.handleRequest(request, jwt);
				chain.doFilter(headers, res);
				return;
			}

			if (ObjectUtil.isBlank(token)) {
				throw new UserTokenAuthException("token is blank");
			}

			Map<String, Long> domains = this.domainService.queryAllDomainID();
			String domain = DomainUtil.getDomain(request, this.domainHeader);

			if (!domains.containsKey(domain)) {
				log.error("使用了非法域名: {} ~ {}", domain, domains);
				throw new IllegalDomainException("使用了非法域名，禁止访问！");
			}

			if (this.jwtTool.isExpired(jwt)) {
				throw new UserTokenAuthException("user token is expired");
			}

			if (this.isMatchUri(reqUri, this.verifyTokenUris)) {
				if (this.authService.isGuest(jwt.getUserId()))
					throw new UserTokenAuthException("user token is invalid");
			}

			if (!isPrefix) {
				throw new AuthException("非法请求(invalid request)!");
			}

			String appCode = this.appCode(reqUri);
			if (appCode == null) {
				throw new AuthException("root path forbidden!");
			}

			AuthVerify authVerify = this.authService.verifyReqAuth(domain, appCode, jwt.getUserId(), jwt.getTenantId(), reqUri, request.getMethod());

			if (authVerify.isAuth()) {
				AuthInfo auth = authVerify.getAuthInfo();
				if (this.isMatchUri(reqUri, this.recLogUris) && auth != null) {
					log.info("{}, {}, {}, {}, {}, {}", IpUtils.getClientIp(request), jwt.getUserId(), auth.getResourceName(), auth.getResourcePath()
							, request.getQueryString(), request.getParameterMap().keySet());
				}
			}
			else {
				if (this.authService.isGuest(jwt.getUserId())) {
					throw new UserTokenAuthException("Forbidden, guest no auth!");
				}
				throw new TokenForbiddenException("Forbidden,no auth!");
			}

			FilterRequestWrapper headers = this.handleRequest(request, jwt);

			chain.doFilter(headers, res);
		} catch (Exception e) {
			HttpServletResponse response = (HttpServletResponse) res;
			String userId = jwt == null ? "" : jwt.getUserId().toString();
			if (e instanceof AuthException)
				this.throwException(response, (AuthException) e, userIP, reqUri, userId);
			else {
				this.throwException(response, new AuthException("请求异常，请重试！"), userIP, reqUri, userId);
				e.printStackTrace();
			}
		}
	}

	private FilterRequestWrapper handleRequest(HttpServletRequest request, JWT jwt) {
		FilterRequestWrapper headers = new FilterRequestWrapper(request);
		UserInfo user = new UserInfo(jwt);
		headers.setUriPrefix(this.proxyPrefix);

		headers.add(Constants.CONTEXT_KEY_USER_ID, ObjectUtil.string(user.getId()));
		headers.add(Constants.CONTEXT_KEY_USER_TENANT, ObjectUtil.string(user.getTenantId()));
		Date d = jwt.getExpireDate();
		headers.add(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME, d == null ? "0" : String.valueOf(jwt.getExpireDate().getTime()));

		return headers;
	}
	
	private boolean isMatchUri(String reqUri, List<String> target) {
		for (String s : target) {
			if (reqUri.startsWith(s))
				return true;
		}
		return false;
	}
	
	private String appCode(String reqUri) {
		try {
			String root = "/";
			if ("".equals(reqUri) || root.equals(ObjectUtil.string(reqUri).trim()))
				return root;

			String[] temp = reqUri.split(root);

			return ObjectUtil.isBlank(temp[1]) ? null : temp[1].toLowerCase();
		} catch (Exception e) {
			log.error("请求截取应用编码异常");
		}
		return null;
	}

	private void throwException(HttpServletResponse response, AuthException ex, String userIP, String reqUri, String userId) {

		try {
			int status = ex.getStatus();
			String message = status == 500 ? "Sorry, the server is abnormal, please try again, or contact the administrator: support#zhiletu.com"
					: ObjectUtil.string(ex.getMessage());
			response.setStatus(status == 500 ? 200 : status);
			String json = this.resJson.ok(new Result(status, message));
			response.getWriter().write(json);
			log.error("userIP: {}, reqUri: {}, userId: {}, exception: {}", userIP, reqUri, userId, message);
		}
		catch (IOException e) {
			log.error("异常处理时异常：{}", e.getMessage());
		}
	}

	@Override
	public void destroy() {
		log.debug("filter is destroy.");
	}
}
