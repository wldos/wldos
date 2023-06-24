/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.core.enums;

/**
 * 用户状态枚举。
 *
 * @author 树悉猿
 * @date 2022/9/5
 * @version 1.0
 */
public enum UserStatusEnum {

	notActive("未激活的", "notActive"),
	locked("已锁定", "locked"),
	cancelled("cancelled", "cancelled"),
	normal("正常", "normal");

	private final String label;

	private final String value;

	UserStatusEnum(String label, String value) {
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