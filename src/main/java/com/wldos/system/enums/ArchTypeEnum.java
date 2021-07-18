/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 体系类型枚举，组织、团队、群组和圈子。同一个公司，同一个体系结构内只能有一套组织机构，即一棵组织树，比如一套人事组织，一套工会，
 * 体系结构定义了某类组织的结构，如人事组织是由机构、部门、岗位和下面人员构成的，再比如群组一般由群主、管理员、组员构成。
 * 体系结构定义了相应组织的模板(模型)，有几种体系结构，就对应几类组织，即体系结构是组织机构的模式定义(schema)。
 * 注意：这里的体系类型是系统默认值，在租户业务体系中需要自己定义。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum ArchTypeEnum {

	/** 平台用户组：平台、管理员、会员、游客。凡是平台定义并设置的组织都是本类型 */
	PLATFORM("platform"),
	/**	租户内组织机构 */
	ORG("org"),
	/** 团队 */
	TEAM("team"),
	/** 群组 */
	GROUP("group"),
	/** 圈子 */
	CIRCLE("circle");

	private final String roleName;

	ArchTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
