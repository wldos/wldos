/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.enums;

/**
 * 记录删除标志枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum DeleteFlagEnum {

	/** 有效。 */
	NORMAL("normal"),
	/** 无效 */
	DELETED("deleted");

	private final String enumName;

	DeleteFlagEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}