/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 平台保留用户身份相关枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserIdentityEnum {
	ORG("org"),
	COM("com"),
	SOCIAL("social"),
	MIDDLE("middle"),
	SERVANT("servant");

	private final String identityName;

	UserIdentityEnum(String identityName) {
		this.identityName = identityName;
	}

	@Override
	public String toString() {
		return this.identityName;
	}
}