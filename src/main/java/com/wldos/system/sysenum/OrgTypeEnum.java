/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 组织类型枚举。
 *
 * @Title OrgTypeEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum OrgTypeEnum {

	/**
	 * 平台专用组：平台、管理员、会员、游客。*/
	platform("platform"),
	/**	租户内组织机构，对于公司就是内部的部门划分 */
	org("org"),
	/** 角色组。 */
	roleOrg("role_org"),
	/** 团队 */
	team("team"),
	/** 群组 */
	group("group"),
	/** 圈子 */
	circle("circle");

	private String roleName;

	OrgTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
