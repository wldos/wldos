/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 用户状态枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserStatusEnum {

	/** 有效。 */
	NORMAL("normal"),
	/** 无效 */
	CANCELLED("cancelled"),
	/** 已锁定 */
	LOCKED("locked");

	private final String enumName;

	UserStatusEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}
