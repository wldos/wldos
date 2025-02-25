/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

/**
 * 用户状态枚举。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
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