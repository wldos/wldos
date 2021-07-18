/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 组织类型枚举，组织架构、团队、群组和圈子，某种类型的组织具备什么构成，由体系结构对应的模型定义。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum OrgTypeEnum {

	/**
	 * 平台专用组：平台、管理员、会员、游客。凡是平台定义并设置的组织都是本类型，由于角色只绑定组织，所以平台组织也一对一地设置相应名称的用户组，
	 * 为了降低维度，比如会员又分为免费会员、普通VIP和年份VIP等，可以在会员下设置3组，角色继承降低授权冗余度，组织分层权限不继承，
	 * 然后分别授予对应的用户组，会员仅做组织节点展示不参与角色绑定，这种授权原则遵循平台运营规则。 用于支撑平台系统管理。*/
	PLATFORM("platform"),
	/**	租户内组织机构，对于公司就是内部的部门划分 */
	ORG("org"),
	/**
	 * 角色组：关联了系统角色的组织，为满足业务设置的虚组织，与正常组织机构不同，在正常的组织人员树中仅用于标识人员角色。
	 * 用于支撑业务经营发生的组织管理。一个org可以拥有多个roleOrg，角色只关联到roleOrg，不直接关联org。
	 * 对应平台的角色组是管理租户权限的角色组，并与平台专用组区分。
	 * */
	roleorg("role_org"),
	/** 团队 */
	TEAM("team"),
	/** 群组 */
	GROUP("group"),
	/** 圈子 */
	CIRCLE("circle");

	private final String roleName;

	OrgTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
