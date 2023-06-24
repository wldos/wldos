/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.core.vo;

/**
 * 当前请求路由是否有权限。约定菜单权限就是路由权限，权限
 *
 * @author 树悉猿
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