/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 角色类型枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum RoleTypeEnum {

	/**	系统角色：平台管理员、普通会员、VIP会员、年费会员，由于角色只绑定组织，所以平台组织也一对一地设置相应名称的用户组 */
	SYS_ROLE("sys_role"),
	/** 社会主体：政府、中介、企业、社团、学生、教师、音乐人... */
	SUBJECT("subject"),
	/** 租户角色：某个租户(公司)在其内部可以分配的业务角色，这种角色来源于平台业务应用，用于关联租户的组织部门来实现权限集成。这属于通用SaaS的顶层设计模式。 */
	TAL_ROLE("tal_role");

	private final String roleName;

	RoleTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
