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
 * 角色资源授权树。
 *
 * @author 元悉宇宙
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
