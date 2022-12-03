/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

import com.wldos.common.Constants;

/**
 * 应用类型枚举，平台级应用属于底层基础支撑驱动系统不允许租户预订，只有普通应用级程序或服务支持租户预订。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum AppTypeEnum {

	/** 平台构成应用 */
	PLATFORM("平台", Constants.ENUM_TYPE_APP_PLAT),
	/** 公开应用 */
	APP("市场", "app"),
	/** 私域应用，比如自研小程序 */
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
