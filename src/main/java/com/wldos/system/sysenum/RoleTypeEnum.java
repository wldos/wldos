/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 角色类型枚举。
 *
 * @Title RoleTypeEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum RoleTypeEnum {

	/**	系统角色 */
	sysRole("sys_role"),
	/** 社会主体 */
	subject("subject"),
	/** 租户角色。 */
	talRole("tal_role");

	private String roleName;

	RoleTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
