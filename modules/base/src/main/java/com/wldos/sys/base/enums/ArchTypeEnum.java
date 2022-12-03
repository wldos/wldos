/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 组织体系结构类型枚举。体系结构以树状字典表实现，因为体系结构是动态变化的，而且体系结构是由所属公司、下级体系结构、包含的组
 * 织机构、角色、群组和用户等构成的一棵树，所以用此枚举来标识体系结构内某个节点的类型（组织机构、体系结构）。
 * 体系结构是由一系列组织机构通过层次关系组成的一个树形结构。一个公司下可根据业务需求存在多个体系机构，如人事组织结构、办公组织
 * 结构、财务组织结构、项目组织结构等。体系结构可以存在上下级关系，这种关系仅限于展现形式，体系结构与体系结构之间没有权限继承，
 * 也就是说在授权管理中同一公司下的体系结构之间全部是扁平关系。
 * 由于体系结构、组织机构分两张表设计，且都是树形结构，所以体系结构表可以只存储体系结构类型，在组织机构表增加所属体系结构属性，
 * 这样可以两张表结合查询到体系结构下的组织机构树，用组织机构树作为子树合并体系结构树上所属体系结构节点。
 * 某种类型的组织构成，由体系结构树定义。
 * 平台用户组、组织、团队、群组和圈子等具体的组织结构是由体系结构树来定义和描述的。
 * 同一个公司，同一个体系结构内只能有一套组织机构，即一棵组织树，比如一套人事组织，一套工会，
 * 体系结构定义了某类组织的结构，如人事组织是由机构、部门、岗位和下面人员构成的，再比如群组一般由群主、管理员、组员构成。
 * 体系结构定义了相应组织的模板(模型)，有几种体系结构，就对应几类组织，即体系结构是组织机构的模式定义(schema)。
 * 体系结构是组织管理中为适应业务领域所作的范式定义（schema），组织类型是从业务维度对组织的类型划分（type），某类型的组织的结构由同类型的体系
 * 结构定义，然后该类型的组织节点挂在体系结构对位的位置上。
 * 注意：这里的体系类型表示平台目前支持的类型，在租户业务体系中根据此类型定义体系结构树，在具体业务场景中可能需要应用开发者声明体系结构schema，
 * 然后在业务中驱动体系结构的不同行为和权限规则，如果是平台应用由平台开发驱动，体系结构schema需要定义相关类型的体系全部结构成员，这类似于角色
 * 权限体系，V1.0暂不做实现，仅作组织机构的结构树定义。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum ArchTypeEnum {

	/**    组织机构 */
	ORG("组织机构", "org"),
	/** 体系结构 */
	ARCH("体系结构", "arch");

	private final String label;

	private final String value;

	ArchTypeEnum(String label, String value) {
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
