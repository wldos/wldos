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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.controller.NoRepoController;
import com.wldos.support.util.ClientUtil;
import com.wldos.support.util.IpUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.auth.exception.AuthException;
import com.wldos.system.auth.exception.TokenForbiddenException;
import com.wldos.system.auth.exception.UserTokenAuthException;
import com.wldos.system.auth.JWT;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.service.AuthService;
import com.wldos.system.enums.UserRoleEnum;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.vo.AuthVerify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服务网关、请求鉴权，反向代理，服务发现和限流等操作客户端，支持ServiceMesh架构。
 * 默认该网关作独立中心化部署，其他服务分布式并通过网关代理。
 *
 * @author 树悉猿
 * @date 2021-04-18
 * @version V1.0
 */
@RestController
public class EdgeGateWay extends NoRepoController {

	@Value("${gateway.proxy.target}")
	private String targetServiceUrl;

	@Value("${gateway.proxy.prefix}")
	private String proxyPrefix;

	private final AuthService authService;

	private final RestService restService;

	private final List<String> excludeUrls;

	public EdgeGateWay(RestService restService, AuthService authService, @Value("${gateway.ignore.path}") String pathIngore) {
		this.restService = restService;
		this.authService = authService;
		this.excludeUrls = Arrays.asList(pathIngore.split(","));
	}

	@CrossOrigin
	@RequestMapping(value = "${gateway.proxy.prefix}/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS })
	public ResponseEntity<String> fetchAll(HttpServletRequest request, HttpServletResponse response) {
		// token认证验签开始
		JWT jwt = null;

		String token = request.getHeader(this.tokenHeader);
		if (ObjectUtil.isBlank(token)) { // 约定客户端未登录时传默认的token，屏蔽非法请求
			getLog().warn("token is blank, client ip is {}", ClientUtil.getClientIp(request));
			throw new UserTokenAuthException("token is blank");
		}
		jwt = jwtTool.popJwt(token);
		request.setAttribute(PubConstants.CONTEXT_KEY_USER, new UserInfo(jwt));

		// 放过免签请求
		String reqUri = request.getRequestURI().replace(this.proxyPrefix, "");

		if (this.excludeUrls.contains(reqUri)) {
			return restService.redirect(request, response, targetServiceUrl, proxyPrefix, this.resJson);
		}

		if (this.jwtTool.isExpired(jwt)) {
			getLog().warn("user token is expired,username={}", jwt.getLoginName());
			throw new UserTokenAuthException("user token is expired");
		}

		String appCode = this.appCode(reqUri);
		if (appCode == null) {
			getLog().warn("root path forbidden! username={}", jwt.getLoginName());
			throw new AuthException("root path forbidden!");
		}
		// 认证通过说明已登录，鉴权开始 （授权在登录模块）
		AuthVerify authVerify = this.authService.verifyReqAuth(appCode, jwt.getUserId(), reqUri, request.getMethod());
		// 仲裁鉴权结果，存在放行，否则终止并警告
		if (authVerify.isAuth()) {
			AuthInfo auth = null;
			if ((auth = authVerify.getAuthInfo()) != null) { // 资源存在，记录日志，后期记入数据库
				getLog().info("{}, {}, {}, {}, {}, {}, {}", IpUtils.getClientIp(request), jwt.getLoginName(), jwt.getUserId(), auth.getResourceName(), auth.getResourcePath()
						, request.getQueryString(), request.getParameterMap());
			}
		}
		else {
			if (UserRoleEnum.GUEST.toString().equals(token)) {
				getLog().warn("Forbidden, guest no auth! username={}", jwt.getLoginName());
				throw new UserTokenAuthException("Forbidden, guest no auth!");
			}
			getLog().warn("Forbidden,no auth! username={}", jwt.getLoginName());
			throw new TokenForbiddenException("Forbidden,no auth!");
		}
		// 鉴权通过，放行！
		return restService.redirect(request, response, targetServiceUrl, proxyPrefix, this.resJson);
	}

	/**
	 * 应用编码最长5位，作为应用在平台上的请求路径前缀。
	 *
	 * @param reqUri 请求资源URI
	 * @return 应用编码
	 */
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
