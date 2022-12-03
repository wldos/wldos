/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

import com.wldos.common.Constants;

/**
 * 组织节点类型枚举，组织架构、团队、群组和圈子，某种类型的组织具备什么构成，由体系结构树定义。
 * 组织类型声明某个组织节点的类型，组织体系结构类型是某类结构的组织体系的类型，体系结构的每个节点下可以挂接标称类型的组织节点，
 * 体系结构是结构的概念，组织节点是一个节点，可能是机构、部门、角色、岗位、人员、团队、用户群。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum OrgTypeEnum {

	/**
	 * 平台专用组（系统用户组）：平台、管理员、会员、游客。凡是平台定义并设置的组织都是本类型，由于角色只绑定组织，所以平台组织也一对一地设置相应名称的用户组，
	 * 为了降低维度，比如会员又分为免费会员、普通VIP和年份VIP等，可以在会员下设置3组，角色继承降低授权冗余度，组织分层权限不继承，
	 * 然后分别授予对应的用户组，会员仅做组织节点展示不参与角色绑定，这种授权原则遵循平台运营规则。 用于支撑平台系统管理。*/
	PLATFORM("系统用户组", Constants.ENUM_TYPE_ORG_PLAT),
	/**    租户内组织机构，对于公司就是内部的部门划分 */
	ORG("组织机构", "org"),
	/**
	 * 角色组：关联了系统角色的组织，为满足业务细分设置的虚组织，是常规组织管理的灵活延伸，在正常的组织人员树中用于分角色管理人员，同时与正常组织区分开来。
	 * 用于支撑业务经营发生的组织管理。一个org可以拥有多个roleOrg，角色应该设置到roleOrg，不应直接设置到org。比如一个群组，有管理员，有组员，用角色组管理
	 * 这些角色人员，具备确定、稳定的角色-组织关系，便于系统实施，权限不与人员强绑定，不受人员流失干扰。虚组织与实组织在具体业务场景中具备不同的行为能力。
	 * 对应平台的角色组是管理租户权限的角色组，并与平台专用组区分。
	 * */
	ROLE_ORG("角色组", "role_org"),
	/** 团队 */
	TEAM("团队", "team"),
	/** 群组 */
	GROUP("群组", "group"),
	/** 圈子 */
	CIRCLE("圈子", "circle");

	private final String label;

	private final String value;

	OrgTypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}
