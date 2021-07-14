/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 体系类型枚举。
 *
 * @Title OrgTypeEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
@Deprecated
public enum ArchTypeEnum {

	/** 平台用户组：平台、管理员、会员、游客。凡是平台定义并设置的组织都是本类型 */
	platform("platform"),
	/**	租户内组织机构 */
	org("org"),
	/** 团队 */
	team("team"),
	/** 群组 */
	group("group"),
	/** 圈子 */
	circle("circle");

	private String roleName;

	ArchTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
