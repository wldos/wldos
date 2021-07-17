/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 平台保留用户角色相关枚举值。角色是业务概念，这里定义的是属于平台保留的角色。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserRoleEnum {

	/**	游客，游客作为保留角色拥有相关权限管理，游客没有数据库用户，只在系统运行时标识为游客角色 */
	GUEST("guest"),
	/** 普通会员 */
	USER("user"),
	/** VIP */
	VIP("vip");

	private final String roleName;

	UserRoleEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
