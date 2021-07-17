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
 * 组织角色授权树。
 *
 * @author 树悉猿
 * @date 2021/5/22
 * @version 1.0
 */
public class OrgRoleTree {
	// 已授权角色
	private List<OrgRole> orgRole;
	// 当前组织角色授权树镜像
	private List<AuthRes> authRes;

	public List<OrgRole> getOrgRole() {
		return orgRole;
	}

	public void setOrgRole(List<OrgRole> orgRole) {
		this.orgRole = orgRole;
	}

	public List<AuthRes> getAuthRes() {
		return authRes;
	}

	public void setAuthRes(List<AuthRes> authRes) {
		this.authRes = authRes;
	}

	public String toString() {
		return "{orgRole: " + orgRole.toString() + ", authRes: " + authRes.toString() + "}";
	}
}
