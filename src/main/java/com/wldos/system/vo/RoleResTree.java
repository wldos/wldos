/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.vo;

import java.util.List;

/**
 * 角色资源授权树。
 *
 * @author 树悉猿
 * @date 2021/5/22
 * @version 1.0
 */
public class RoleResTree {
	// 已授权资源
	private List<RoleRes> roleRes;
	// 当前角色资源授权树镜像
	private List<AuthRes> authRes;

	public List<RoleRes> getRoleRes() {
		return roleRes;
	}

	public void setRoleRes(List<RoleRes> roleRes) {
		this.roleRes = roleRes;
	}

	public List<AuthRes> getAuthRes() {
		return authRes;
	}

	public void setAuthRes(List<AuthRes> authRes) {
		this.authRes = authRes;
	}

	public String toString() {
		return "{roleRes: " + roleRes.toString() + ", authRes: " + authRes.toString() + "}";
	}
}
