/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.vo;

import java.util.List;

/**
 * 角色资源授权树。
 *
 * @Title RoleResTree
 * @Package com.wldos.system.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/22
 * @Version 1.0
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
