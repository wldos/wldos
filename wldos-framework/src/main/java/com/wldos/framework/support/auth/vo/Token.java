/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 认证通过后返回的token。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
public class Token {
	private String accessToken;

	/** 前端token过期分钟数，后端token的刷新时长*/
	private int refresh;

	/** JWT对象，用于记录日志等操作*/
	@JsonIgnore
	private JWT jwt;

	public Token() {
	}

	public Token(String accessToken) {
		this.accessToken = accessToken;
	}

	public Token(String accessToken, int refresh) {
		this.accessToken = accessToken;
		this.refresh = refresh;
	}

	public Token(String accessToken, int refresh, JWT jwt) {
		this.accessToken = accessToken;
		this.refresh = refresh;
		this.jwt = jwt;
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

	public JWT getJwt() {
		return jwt;
	}

	public void setJwt(JWT jwt) {
		this.jwt = jwt;
	}
}