/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;

/**
 * 组织角色授权树。
 *
 * @author 元悉宇宙
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
