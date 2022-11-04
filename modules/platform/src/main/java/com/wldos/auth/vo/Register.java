/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.vo;

/**
 * 注册信息。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
public class Register {
	private long id;

	private String loginName; // 账号,默认邮箱

	private String nickname; // 昵称（平台上显示名称）

	private String email;

	private String passwd;

	private String mobile;

	private String registerIp;

	private String verifyCode;

	/** 确认密码 */
	private String confirm;

	private String prefix;

	public Register() {}

	public static Register of(Long uid, String loginName, String nickname, String registerIp) {
		return new Register(uid, loginName, nickname, registerIp);
	}

	private Register(Long uid, String loginName, String nickname, String registerIp) {
		this.id = uid;
		this.loginName = loginName;
		this.nickname = nickname;
		this.registerIp = registerIp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
