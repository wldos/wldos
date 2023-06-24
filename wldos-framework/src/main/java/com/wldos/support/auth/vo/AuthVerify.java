/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth.vo;

import com.wldos.support.resource.vo.AuthInfo;

/**
 * 资源请求权限检查器。
 *
 * @author 树悉猿
 * @date 2021/4/26
 * @version 1.0
 */
public class AuthVerify {
	private AuthInfo authInfo;

	private boolean auth;

	public AuthInfo getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(AuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	/**
	 * 是否有权限
	 */
	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}
}
