/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 平台保留用户角色相关枚举值。
 *
 * @Title UserRoleEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum UserRoleEnum {

	/**	游客 */
	guest("guest"),
	/** 普通会员 */
	user("user"),
	/** VIP */
	vip("vip");

	private String roleName;

	UserRoleEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
