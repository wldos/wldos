/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.vo;

/**
 * oauth2.0社会化登录信息。
 *
 * @author 树悉猿
 * @date 2022-10-13
 * @version V1.0
 */
public class OAuthLoginParams {
	/** 请求oauth token的授权码 */
	private String code;

	/** 保持请求和回调状态，授权请求后原样带回给第三方。用于防止csrf攻击，第三方请求时生成，回传时验证，逻辑同手机验证码 */
	private String state;

	/** OAuth2.0服务商类型，微信、qq、微博等，融合于登录类型（账号登录、手机号登录） */
	private String authType;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}
}