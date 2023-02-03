/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 社会化登录配置信息。
 *
 * @author 树悉猿
 * @date 2022/10/19
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthConfig {
	private String appId;
	private String appSecret;
	private String redirectUri;
	private String scope;
	private String codeUri;
	private String accessTokenUri;
	private String refreshTokenUri;
	private String userInfoUri;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
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

	public String getCodeUri() {
		return codeUri;
	}

	public void setCodeUri(String codeUri) {
		this.codeUri = codeUri;
	}

	public String getAccessTokenUri() {
		return accessTokenUri;
	}

	public void setAccessTokenUri(String accessTokenUri) {
		this.accessTokenUri = accessTokenUri;
	}

	public String getRefreshTokenUri() {
		return refreshTokenUri;
	}

	public void setRefreshTokenUri(String refreshTokenUri) {
		this.refreshTokenUri = refreshTokenUri;
	}

	public String getUserInfoUri() {
		return userInfoUri;
	}

	public void setUserInfoUri(String userInfoUri) {
		this.userInfoUri = userInfoUri;
	}
}