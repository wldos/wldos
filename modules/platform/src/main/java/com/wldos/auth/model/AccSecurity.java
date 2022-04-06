/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.model;

/**
 * 账户安全设置。
 *
 * @author 树悉猿
 * @date 2021/9/22
 * @version 1.0
 */
public class AccSecurity {
	/** 密码强度 */
	private String passStatus;

	/** 密保手机 */
	private String mobile;

	/** 密保问题 */
	private String secQuest;

	/** 备用邮箱 */
	private String bakEmail;

	/** mfa设备 */
	private String mfa;

	public String getPassStatus() {
		return passStatus;
	}

	public void setPassStatus(String passStatus) {
		this.passStatus = passStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSecQuest() {
		return secQuest;
	}

	public void setSecQuest(String secQuest) {
		this.secQuest = secQuest;
	}

	public String getBakEmail() {
		return bakEmail;
	}

	public void setBakEmail(String bakEmail) {
		this.bakEmail = bakEmail;
	}

	public String getMfa() {
		return mfa;
	}

	public void setMfa(String mfa) {
		this.mfa = mfa;
	}
}
