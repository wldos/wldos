/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.wldos.auth.service.LoginAuthService;
import com.wldos.auth.vo.ActiveParams;
import com.wldos.auth.vo.BakEmailModifyParams;
import com.wldos.auth.vo.MFAModifyParams;
import com.wldos.auth.vo.PasswdModifyParams;
import com.wldos.auth.vo.PasswdResetParams;
import com.wldos.auth.vo.Register;
import com.wldos.auth.vo.SecQuestModifyParams;
import com.wldos.base.controller.NoRepoController;
import com.wldos.auth.vo.Login;
import com.wldos.auth.vo.LoginAuthParams;
import com.wldos.auth.vo.MobileModifyParams;
import com.wldos.sys.core.vo.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RefreshScope
@RequestMapping("login")
@RestController
public class LoginAuthController extends NoRepoController {
	private final LoginAuthService loginAuthService;

	public LoginAuthController(LoginAuthService loginAuthService) {
		this.loginAuthService = loginAuthService;
	}

	@PostMapping("account")
	public Login loginAuth(HttpServletRequest request, @RequestBody LoginAuthParams loginAuthParams, @Value("${passwd.hexKey.code}") String hexKeyCode) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		Login user = this.loginAuthService.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request, hexKeyCode);
		if (user == null) {
			getLog().info("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setNews("登陆失败，请重试！");
			user.setType(loginAuthParams.getType());
		}
		return user;
	}

	@PostMapping("login4mobile")
	public Login loginAuthMobile(HttpServletRequest request, @RequestBody LoginAuthParams loginAuthParams, @Value("${passwd.hexKey.code}") String hexKeyCode) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		// todo 改造为手机验证码登录验证逻辑
		Login user = this.loginAuthService.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request, hexKeyCode);
		if (user == null) {
			getLog().info("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setNews("登陆失败，请重试！");
			user.setType(loginAuthParams.getType());
		}
		return user;
	}

	@DeleteMapping("logout")
	public User logout(@RequestHeader(value = "${token.access.header}") String token) {
		return this.loginAuthService.logout(token, this.getDomainId());
	}

	@PostMapping("register")
	public Login register(@RequestBody Register register) {
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		getLog().info("register= {} ", register.getLoginName());
		return this.loginAuthService.register(this.getDomain(), this.getDomainId(), register, this.request);
	}

	@PostMapping("active")
	public Login active(@RequestBody ActiveParams active) {
		return this.loginAuthService.active(this.getDomain(), active, this.request);
	}

	@PostMapping("reset")
	public Login resetPasswd(@RequestBody PasswdResetParams resetParams) {

		getLog().info("用户登录名: {} 密码重置 ", resetParams.getLoginName());
		Login user = this.loginAuthService.resetPasswd(resetParams);
		if (user == null) {
			getLog().info("{} 密码重置失败", resetParams.getLoginName());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码重置失败，请重试！");
		}
		return user;
	}

	@PostMapping("passwd")
	public Login changePasswd(@RequestBody PasswdModifyParams passwdModifyParams,
			@Value("${passwd.hexKey.code}") String hexKeyCode) {

		getLog().info("用户id: {} 密码修改 ", passwdModifyParams.getId());
		Login user = this.loginAuthService.changePasswd(passwdModifyParams, hexKeyCode);
		if (user == null) {
			getLog().info("{} 密码修改失败", passwdModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码修改失败，请重试！");
		}
		return user;
	}

	@PostMapping("mobile")
	public Login changeMobile(@RequestBody MobileModifyParams mobileModifyParams) {
		getLog().info("用户id: {} 密保手机修改 ", mobileModifyParams.getId());
		Login user = this.loginAuthService.changeMobile(mobileModifyParams);
		if (user == null) {
			getLog().info("{} 密保手机修改失败", mobileModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保手机修改失败，请重试！");
		}
		return user;
	}

	@PostMapping("secQuest")
	public Login changeSecQuest(@RequestBody SecQuestModifyParams params) {
		getLog().info("用户id: {} 密保问题修改 ", params.getId());
		Login user = this.loginAuthService.changeSecQuest(params);
		if (user == null) {
			getLog().info("{} 密保问题修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保问题修改失败，请重试！");
		}
		return user;
	}

	@PostMapping("bakEmail")
	public Login changeBakEmail(@RequestBody BakEmailModifyParams params) {
		getLog().info("用户id: {} 备用邮箱修改 ", params.getId());
		Login user = this.loginAuthService.changeBakEmail(params);
		if (user == null) {
			getLog().info("{} 备用邮箱修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("备用邮箱修改失败，请重试！");
		}
		return user;
	}

	@PostMapping("mfa")
	public Login changeMFA(@RequestBody MFAModifyParams params) {
		getLog().info("用户id: {} 密保设备修改 ", params.getId());
		Login user = this.loginAuthService.changeMFA(params);
		if (user == null) {
			getLog().info("{} 密保设备修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保设备修改失败，请重试！");
		}
		return user;
	}
}
