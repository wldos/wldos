/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 平台保留用户角色相关枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserRoleEnum {
	
	GUEST("guest"),
	
	USER("user"),
	
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
