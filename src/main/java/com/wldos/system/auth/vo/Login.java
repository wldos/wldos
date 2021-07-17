/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.vo;

import java.util.List;

/**
 * 登录后返回用户认证和权限信息。
 *
 * @author 树悉猿
 * @date 2021-04-30
 * @version V1.0
 */
public class Login {
	/** 登录状态 */
	private String status;
	/** 访问token和刷新token */
	private Token token;
	/** 用户的权限：默认排除菜单权限 */
	private List<String> currentAuthority;
	/** 登录类型：account、mobile */
	private String type;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public List<String> getCurrentAuthority() {
		return currentAuthority;
	}

	public void setCurrentAuthority(List<String> currentAuthority) {
		this.currentAuthority = currentAuthority;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}