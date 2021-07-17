/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.wldos.support.controller.NoRepoController;
import com.wldos.system.auth.dto.LoginAuthParams;
import com.wldos.system.auth.vo.Login;
import com.wldos.system.auth.vo.Register;
import com.wldos.system.vo.User;
import com.wldos.system.auth.service.LoginAuthService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录相关认证、授权controller。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
@RequestMapping("login")
@RestController
public class LoginAuthController extends NoRepoController {
	private final LoginAuthService loginAuthService;

	public LoginAuthController(LoginAuthService loginAuthService) {
		this.loginAuthService = loginAuthService;
	}

	@PostMapping("account")
	public String loginAuth(HttpServletRequest request, @RequestBody LoginAuthParams loginAuthParams, @Value("${passwd.hexkey.code}") String hexKeyCode) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		Login user = this.loginAuthService.login(loginAuthParams, request, hexKeyCode);
		if (user == null) {
			getLog().info("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setType("account");
		}
		return resJson.ok(user);
	}

	@DeleteMapping("logout")
	public String logout(@RequestHeader(value="${token.access.header}") String token) {
		User user = this.loginAuthService.logout(token);
		return resJson.ok(user);
	}

	@GetMapping("refresh")
	public String refreshToken() {
		Login refreshedToken = this.loginAuthService.refreshToken(this.getToken());
		return resJson.ok(refreshedToken);
	}

	@PostMapping("register")
	public String register(@RequestBody Register register) {
		getLog().info("register= {} ", register);
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		Login user = this.loginAuthService.register(register, this.request);
		return resJson.ok(user);
	}
}
