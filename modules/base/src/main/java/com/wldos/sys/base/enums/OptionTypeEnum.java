/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 配置选项类型。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum OptionTypeEnum {

	AUTO_RELOAD("自动加载", "auto_reload"),
	NORMAL("按需加载", "normal");

	private final String label;

	private final String value;

	OptionTypeEnum(String label, String value) {
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