/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.wldos.support.controller.NoRepoController;
import com.wldos.system.auth.param.LoginAuthParams;
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
 * @Title LoginAuthController
 * @Package com.wldos.system.auth.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/29
 * @Version 1.0
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

		this.getLog().info(loginAuthParams.getUsername() + " login in ");
		Login user = this.loginAuthService.login(loginAuthParams, request, hexKeyCode);
		if (user == null) {
			this.getLog().info(loginAuthParams.getUsername() + " 登录失败");
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
		log.info("register="+register);
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		Login user = this.loginAuthService.register(register, this.request);
		return resJson.ok(user);
	}
}
