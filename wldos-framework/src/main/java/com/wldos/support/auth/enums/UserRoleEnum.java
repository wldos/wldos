/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth.enums;

import com.wldos.common.Constants;

/**
 * 平台保留用户角色相关枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserRoleEnum {

	GUEST(Constants.ENUM_TYPE_ROLE_GUEST),
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