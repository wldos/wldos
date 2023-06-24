/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.auth.vo;

import java.io.Serializable;

/**
 * 基于登录参数封装的对象。
 *
 * @author 树悉猿
 * @date 2021/4/29
 * @version 1.0
 */
public class LoginAuthParams implements Serializable {
	/** 登录用户名 */
	private String username;

	private String password;

	/** 验证码: 图形验证码+uuid */
	private String verifyCode;

	/** 手机号 */
	private String mobile;

	/** 手机验证码 */
	private String captcha;

	/** 自动登录 */
	private String autoLogin;

	/** 登录方式：账号密码、手机 */
	private String type;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getAutoLogin() {
		return autoLogin;
	}

	public void setAutoLogin(String autoLogin) {
		this.autoLogin = autoLogin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
