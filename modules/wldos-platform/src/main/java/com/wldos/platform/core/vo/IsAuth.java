/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

/**
 * 当前请求路由是否有权限。约定菜单权限就是路由权限，权限
 *
 * @author 元悉宇宙
 * @date 2023-04-6
 * @version V1.0
 */
public class IsAuth {

	private boolean isAuth;

	public boolean isAuth() {
		return isAuth;
	}

	public void setAuth(boolean auth) {
		isAuth = auth;
	}
}