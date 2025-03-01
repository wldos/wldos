/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth.vo;

import com.wldos.platform.support.resource.vo.AuthInfo;

/**
 * 资源请求权限检查器。
 *
 * @author 元悉宇宙
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
