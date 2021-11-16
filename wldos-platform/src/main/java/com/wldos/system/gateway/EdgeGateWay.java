/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.gateway;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.controller.NoRepoController;
import com.wldos.support.util.ClientUtil;
import com.wldos.support.util.IpUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.Constants;
import com.wldos.support.util.domain.DomainUtil;
import com.wldos.system.auth.exception.AuthException;
import com.wldos.system.auth.exception.TokenForbiddenException;
import com.wldos.system.auth.exception.UserTokenAuthException;
import com.wldos.system.auth.JWT;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.exception.IllegalDomainException;
import com.wldos.system.core.service.AuthService;
import com.wldos.system.core.service.DomainService;
import com.wldos.system.enums.UserRoleEnum;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.vo.AuthVerify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * 微服务网关、请求鉴权，反向代理，服务发现和限流等操作客户端，支持ServiceMesh架构。
 * 默认该网关作独立中心化部署，其他服务分布式并通过网关代理。
 *
 * @author 树悉猿
 * @date 2021-04-18
 * @version V1.0
 */
@SuppressWarnings("unused")
public class EdgeGateWay extends NoRepoController {

	@Value("${gateway.proxy.target}")
	private String targetServiceUrl;

	@Value("${gateway.proxy.prefix}")
	private String proxyPrefix;

	private final DomainService domainService;

	private final AuthService authService;

	private final RestService restService;

	private final List<String> excludeUrls;

	public EdgeGateWay(DomainService domainService, RestService restService, AuthService authService, @Value("${gateway.ignore.path}") String pathIgnore) {
		this.domainService = domainService;
		this.restService = restService;
		this.authService = authService;
		this.excludeUrls = Arrays.asList(pathIgnore.split(","));
	}

	@CrossOrigin
	public ResponseEntity<String> fetchAll(HttpServletRequest request, HttpServletResponse response) {

		JWT jwt;

		String token = request.getHeader(this.tokenHeader);
		if (ObjectUtil.isBlank(token)) {
			getLog().info("token is blank, client ip is {}", ClientUtil.getClientIp(request));
			throw new UserTokenAuthException("token is blank");
		}

		Map<String, Long> domains = this.domainService.queryAllDomainID();
		String domain = DomainUtil.getDomain(request, this.domainHeader);
		if (!domains.containsKey(this.getDomain())) {
			getLog().info("使用了非法域名: {} ~ {}", this.getDomain(), domains);
			throw new IllegalDomainException("使用了非法域名，禁止访问！");
		}

		jwt = jwtTool.popJwt(token);
		request.setAttribute(Constants.CONTEXT_KEY_USER, new UserInfo(jwt));

		String reqUri = request.getRequestURI().replace(this.proxyPrefix, "");

		if (this.excludeUrls.contains(reqUri)) {
			return restService.redirect(request, response, targetServiceUrl, proxyPrefix);
		}

		if (this.jwtTool.isExpired(jwt)) {
			getLog().warn("user token is expired,userId={}", jwt.getUserId());
			throw new UserTokenAuthException("user token is expired");
		}

		String appCode = this.appCode(reqUri);
		if (appCode == null) {
			getLog().warn("root path forbidden! userId={}", jwt.getUserId());
			throw new AuthException("root path forbidden!");
		}

		AuthVerify authVerify = this.authService.verifyReqAuth(domain, appCode, jwt.getUserId(), jwt.getTenantId(), reqUri, request.getMethod());

		if (authVerify.isAuth()) {
			AuthInfo auth = authVerify.getAuthInfo();
			if (auth != null) {
				getLog().info("{}, {}, {}, {}, {}, {}", IpUtils.getClientIp(request), jwt.getUserId(), auth.getResourceName(), auth.getResourcePath()
						, request.getQueryString(), request.getParameterMap().entrySet());
			}
		}
		else {
			if (UserRoleEnum.GUEST.toString().equals(token)) {
				getLog().warn("Forbidden, guest no auth! userId={}", jwt.getUserId());
				throw new UserTokenAuthException("Forbidden, guest no auth!");
			}
			getLog().warn("Forbidden,no auth! userId={}", jwt.getUserId());
			throw new TokenForbiddenException("Forbidden,no auth!");
		}

		return restService.redirect(request, response, targetServiceUrl, proxyPrefix);
	}

	private String appCode(String reqUri) {
		try {
			if ("".equals(reqUri) || "/".equals(ObjectUtil.string(reqUri).trim()))
				return "/";

			String[] temp = reqUri.split("/");

			return ObjectUtil.isBlank(temp[1]) ? null : temp[1].toLowerCase();
		} catch (Exception e) {
			getLog().error("请求截取应用编码异常");
		}
		return null;
	}
}
