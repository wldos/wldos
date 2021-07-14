/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.vo;

/**
 * 用户登录后返回的token。。
 *
 * @Title Token
 * @Package com.wldos.system.auth.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/30
 * @Version 1.0
 */
public class Token {
	private String accessToken;
	// private String refreshToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
