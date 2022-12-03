/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.wldos.auth.vo.Login;
import com.wldos.auth2.model.OAuthConfig;
import com.wldos.auth2.vo.OAuthLoginParams;
import com.wldos.sys.base.enums.OAuthTypeEnum;
import com.wldos.auth2.service.OAuthService;
import com.wldos.base.controller.NoRepoController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2.0相关控制器。
 * 第一步，跳转到OAuth服务提供商在线服务获取授权码(authorization_code)，作为一个凭据执行后续操作。
 * https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
 * 执行上面的请求后，会返回code，并追加到重定向uri后面，同时保留请求时附带的state随机码密文：
 * e.g. https://www.wldos.com/user/login?code=041Uk8Ga13Vr4E0iI1Ha1BKdX53Uk8Gy&state=133255
 *
 * 第二步，重定向uri是客户端的服务端提供的异步监听服务，它在后端用返回的code作为进一步请求OAuth服务提供商的访问token的凭据，同时验证附带的state参数是否和第一步的备份一致，不一致
 * 认为是非法请求（csrf攻击）拒绝访问，一致则继续执行如下的请求，OAuth服务提供商返回访问token给客户端的后端服务作为进一步获取用户信息的token。
 * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
 *
 * 第三步，调用token是否过期api判断是否需要刷新，若过期执行如下的刷新请求
 * https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
 *
 * 授权作用域（scope）	接口		接口说明
 * snsapi_base	/sns/auth	检查access_token有效性
 * snsapi_base	/sns/oauth2/access_token	通过 code 换取access_token、refresh_token和已授权scope
 * snsapi_base	/sns/oauth2/refresh_token	刷新或续期access_token使用
 * snsapi_userinfo	/sns/userinfo	获取用户个人信息
 *
 * @author 树悉猿
 * @date 2022/10/13
 * @version 1.0
 */
@RestController
@RequestMapping("oauth")
public class OAuthController extends NoRepoController {

	private final OAuthService oAuthService;

	public OAuthController(OAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}

	/**
	 * 获取授权码申请url
	 * @param authType 社会化登录类型
	 * @param redirectPrefix 请求三方登录的重定向url前缀，三方登录默认只能授权一个域，对于非授权域的三方登录提供共享支持
	 * @return 授权码申请url
	 */
	@GetMapping("code/{authType}")
	public String getCodeUrl(@PathVariable String authType, @RequestParam String redirectPrefix) throws UnsupportedEncodingException {
		// 创建授权码申请链接
		return OAuthTypeEnum.match(authType) ? this.oAuthService.genAuthCodeUrl(authType, redirectPrefix) : "";
	}

	@PostMapping("login")
	public Login loginAuth(@RequestBody OAuthLoginParams loginAuthParams) throws IOException {

		getLog().info("{} OAuth login in {}", loginAuthParams.getAuthType(), this.getUserIp());
		Login user = this.oAuthService.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request,
				response, this.getUserIp());
		if (user == null) {
			getLog().info("{} 三方登录失败", loginAuthParams.getAuthType());
			user = new Login();
			user.setStatus("error");
			user.setNews("三方登陆失败，请重试！");
			user.setType(loginAuthParams.getAuthType());
		}
		return user;
	}

	/**
	 * 社会化登录配置
	 *
	 * @param config 配置参数
	 * @param authType 社会化登录类型
	 */
	@PostMapping("config/{authType}")
	public void configOAuth(@RequestBody OAuthConfig config, @PathVariable String authType) {
		if (OAuthTypeEnum.match(authType))
			this.oAuthService.configOAuth(authType, config);
		else
			throw new RuntimeException("提交了未知类型，系统已拒绝");
	}

	/**
	 * 取回社会化登录配置
	 *
	 * @param authType 社会化登录类型
	 * @return 配置参数
	 */
	@GetMapping("fetch/{authType}")
	public OAuthConfig fetchConfig(@PathVariable String authType) {
		if (OAuthTypeEnum.match(authType))
			return this.oAuthService.fetchOAuthConfig(authType);
		else
			throw new RuntimeException("提交了未知类型，系统已拒绝");
	}
}
