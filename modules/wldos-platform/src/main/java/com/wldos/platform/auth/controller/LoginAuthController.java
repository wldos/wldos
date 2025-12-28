/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.wldos.common.res.Result;
import com.wldos.platform.auth.service.LoginAuthService;
import com.wldos.platform.auth.vo.ActiveParams;
import com.wldos.platform.auth.vo.BakEmailModifyParams;
import com.wldos.platform.auth.vo.Login;
import com.wldos.platform.auth.vo.LoginAuthParams;
import com.wldos.platform.auth.vo.MFAModifyParams;
import com.wldos.platform.auth.vo.MobileModifyParams;
import com.wldos.platform.auth.vo.PasswdModifyParams;
import com.wldos.platform.auth.vo.PasswdResetParams;
import com.wldos.platform.auth.vo.Register;
import com.wldos.platform.auth.vo.SecQuestModifyParams;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.common.Constants;
import com.wldos.platform.support.auth.KeyConfig;
import com.wldos.platform.core.vo.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 登录相关认证、授权controller。
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@Api(tags = "登录认证")
@RefreshScope
@RequestMapping("login")
@RestController
public class LoginAuthController extends NonEntityController<LoginAuthService> {
	private final KeyConfig keyConfig;

	public LoginAuthController(KeyConfig keyConfig) {
		this.keyConfig = keyConfig;
	}

	@ApiOperation(value = "获取加密公钥", notes = "获取RSA公钥用于前端加密密码")
	@GetMapping("encrypt")
	public Result fetchPubKey() {
		byte[] pubKey = this.keyConfig.getUserPubKey();
		return Result.ok(pubKey == null ? "" : KeyConfig.toHexString(pubKey));
	}

	@ApiOperation(value = "账户密码登录", notes = "使用用户名和密码进行登录")
	@PostMapping("account")
	public Login loginAuth(HttpServletRequest request, @ApiParam(value = "登录参数", required = true) @Valid @RequestBody LoginAuthParams loginAuthParams) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		Login user = this.service.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request);
		if (user == null) {
			getLog().warn("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setNews("登陆失败，请重试！");
			user.setType(loginAuthParams.getType());
		}
		return user;
	}

	@ApiOperation(value = "手机验证码登录", notes = "使用手机号和验证码进行登录")
	@PostMapping("login4mobile")
	public Login loginAuthMobile(HttpServletRequest request, @ApiParam(value = "登录参数", required = true) @Valid @RequestBody LoginAuthParams loginAuthParams) {

		getLog().info("{} login in ", loginAuthParams.getUsername());
		// todo 改造为手机验证码登录验证逻辑
		Login user = this.service.login(this.getDomain(), this.getDomainId(), this.getTenantId(), loginAuthParams, request);
		if (user == null) {
			getLog().warn("{} 登录失败", loginAuthParams.getUsername());
			user = new Login();
			user.setStatus("error");
			user.setNews("登陆失败，请重试！");
			user.setType(loginAuthParams.getType());
		}
		return user;
	}

	@ApiOperation(value = "退出登录", notes = "用户退出登录，清除token")
	@DeleteMapping("logout")
	public User logout(@ApiParam(value = "访问令牌", required = true) @RequestHeader(value = Constants.TOKEN_ACCESS_HEADER) String token) {
		return this.service.logout(token, this.getDomainId());
	}

	@ApiOperation(value = "用户注册", notes = "新用户注册账号")
	@PostMapping("register")
	public Login register(@ApiParam(value = "注册参数", required = true) @Valid @RequestBody Register register) {
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		getLog().info("register= {} ", register.getLoginName());
		return this.service.register(this.getDomain(), this.getDomainId(), register, this.request);
	}

	@ApiOperation(value = "检查密码强度", notes = "检查密码强度等级")
	@GetMapping("passwd/status")
	public String passwdStatusCheck(@ApiParam(value = "密码", required = true) @RequestParam String passwd) {
		String status = this.service.passwdStatusCheck(passwd);
		
		return this.resJson.ok("status", status);
	}

	@ApiOperation(value = "激活账号", notes = "通过激活码激活用户账号")
	@PostMapping("active")
	public Login active(@ApiParam(value = "激活参数", required = true) @Valid @RequestBody ActiveParams active) {
		return this.service.active(this.getDomain(), active, this.request);
	}

	@ApiOperation(value = "重置密码", notes = "通过邮箱验证码重置用户密码")
	@PostMapping("reset")
	public Login resetPasswd(@ApiParam(value = "重置密码参数", required = true) @Valid @RequestBody PasswdResetParams resetParams) {

		getLog().info("用户登录名: {} 密码重置 ", resetParams.getLoginName());
		Login user = this.service.resetPasswd(resetParams);
		if (user == null) {
			getLog().warn("{} 密码重置失败", resetParams.getLoginName());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码重置失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密码", notes = "用户修改登录密码")
	@PostMapping("passwd")
	public Login changePasswd(@ApiParam(value = "修改密码参数", required = true) @Valid @RequestBody PasswdModifyParams passwdModifyParams,
			@Value("${passwd.hexKey.code}") String hexKeyCode) {

		getLog().info("用户id: {} 密码修改 ", passwdModifyParams.getId());
		Login user = this.service.changePasswd(passwdModifyParams, hexKeyCode);
		if (user == null) {
			getLog().warn("{} 密码修改失败", passwdModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密保手机", notes = "用户修改密保手机号")
	@PostMapping("mobile")
	public Login changeMobile(@ApiParam(value = "修改手机参数", required = true) @Valid @RequestBody MobileModifyParams mobileModifyParams) {
		getLog().info("用户id: {} 密保手机修改 ", mobileModifyParams.getId());
		Login user = this.service.changeMobile(mobileModifyParams);
		if (user == null) {
			getLog().warn("{} 密保手机修改失败", mobileModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保手机修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密保问题", notes = "用户修改密保问题")
	@PostMapping("secQuest")
	public Login changeSecQuest(@ApiParam(value = "修改密保问题参数", required = true) @Valid @RequestBody SecQuestModifyParams params) {
		getLog().info("用户id: {} 密保问题修改 ", params.getId());
		Login user = this.service.changeSecQuest(params);
		if (user == null) {
			getLog().warn("{} 密保问题修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保问题修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改备用邮箱", notes = "用户修改备用邮箱")
	@PostMapping("bakEmail")
	public Login changeBakEmail(@ApiParam(value = "修改备用邮箱参数", required = true) @Valid @RequestBody BakEmailModifyParams params) {
		getLog().info("用户id: {} 备用邮箱修改 ", params.getId());
		Login user = this.service.changeBakEmail(params);
		if (user == null) {
			getLog().warn("{} 备用邮箱修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("备用邮箱修改失败，请重试！");
		}
		return user;
	}

	@ApiOperation(value = "修改密保设备", notes = "用户修改MFA密保设备")
	@PostMapping("mfa")
	public Login changeMFA(@ApiParam(value = "修改MFA参数", required = true) @Valid @RequestBody MFAModifyParams params) {
		getLog().info("用户id: {} 密保设备修改 ", params.getId());
		Login user = this.service.changeMFA(params);
		if (user == null) {
			getLog().warn("{} 密保设备修改失败", params.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密保设备修改失败，请重试！");
		}
		return user;
	}
}
