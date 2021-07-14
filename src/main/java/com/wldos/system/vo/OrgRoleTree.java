/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.vo;

import java.util.List;

/**
 * 组织角色授权树。
 *
 * @Title OrgRoleTree
 * @Package com.wldos.system.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/22
 * @Version 1.0
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
