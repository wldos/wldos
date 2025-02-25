/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth.vo;

/**
 * 认证通过后返回的token。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
public class Token {
	private String accessToken;

	/** 过期分钟数， token的刷新周期*/
	private int refresh;

	public Token() {
	}

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