/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
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
@JsonIgnoreProperties(ignoreUnknown = true) // 防止多余未知字段导致的转换异常
public class OAuthConfig {
	/** appId，当前应用在OAuth服务提供商注册的应用id */
	private String appId;
	/** app私钥，注意不要pop到客户端，仅服务端使用 */
	private String appSecret;
	/** 生成重定向链接的，用于获取授权码后重定向到当前应用的后端处理服务，wldos-oauth2托管api，只需要配置如http://www.wldos.com即可 */
	private String redirectUri;
	/** 应用授权作用域，拥有多个作用域用逗号分隔，如微信开放平台网页应用仅填写snsapi_login */
	private String scope;
	/** 首次获取授权码的uri模板(开发者配置选项) */
	private String codeUri;
	/** access token uri模板(开发者配置选项) */
	private String accessTokenUri;
	/** refresh token uri模板(开发者配置选项) */
	private String refreshTokenUri;
	/** 请求用户信息uri模板 */
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
