/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 模板类型枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum TempTypeEnum {
	PRODUCT("产品", "product"),
	ARCHIVES("存档", "archives"),
	CATEGORY("类目", "category"),
	INFO("信息", "info"),
	STATIC("静态", "static"),
	UNKNOWN("未知", "unknown");

	private final String label;
	private final String value;

	TempTypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}