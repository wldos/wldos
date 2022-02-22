/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.enums;

/**
 * 是否判定枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum BoolEnum {

	/** 是 */
	YES("1"),
	/** 否 */
	NO("0");

	private final String enumValue;

	BoolEnum(String enumValue) {
		this.enumValue = enumValue;
	}

	@Override
	public String toString() {
		return this.enumValue;
	}
}
