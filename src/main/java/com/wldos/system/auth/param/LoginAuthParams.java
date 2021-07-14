/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.param;

import java.io.Serializable;

/**
 * 基于登录参数封装的对象。
 *
 * @Title LoginAuthParams
 * @Package com.wldos.system.auth.param
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/29
 * @Version 1.0
 */
public class LoginAuthParams implements Serializable {
	/** 登录用户名 */
	private String username;
	private String password;
	/** 验证码 */
	private String verifycode;
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

	public String getVerifycode() {
		return verifycode;
	}

	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
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
