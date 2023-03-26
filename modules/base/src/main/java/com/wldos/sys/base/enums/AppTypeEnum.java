/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

import com.wldos.common.Constants;

/**
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum AppTypeEnum {

	PLATFORM("平台", Constants.ENUM_TYPE_APP_PLAT),
	APP("市场", "app"),
	PRIVATE("私有", "private");

	private final String label;

	private final String value;

	AppTypeEnum(String label, String value) {
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