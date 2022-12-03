/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth.vo;

/**
 * 认证通过后返回的token。。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
public class Token {
	private String accessToken;

	/** 过期分钟数， token的刷新周期*/
	private int refresh;

	public Token() {}

	public Token(String accessToken) {
		this.accessToken = accessToken;
	}

	public Token(String accessToken, int refresh) {
		this.accessToken = accessToken;
		this.refresh = refresh;
	}

	public String getAccessToken() {
		return accessToken;
	}

	void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getRefresh() {
		return refresh;
	}

	void setRefresh(int refresh) {
		this.refresh = refresh;
	}
}
