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
public class OAuth {
	/** 在oauth服务平台注册的应用id */
	private String appId;

	/** 重定向url */
	private String redirectUri;

	/** 应用授权作用域，拥有多个作用域用逗号分隔，微信开放平台网页应用仅填写snsapi_login */
	private String scope = "snsapi_login";

	/** 保持请求和回调状态，授权请求后原样带回给第三方。用于防止csrf攻击，第三方请求时生成，回传时验证，逻辑同手机验证码 */
	private String state;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}