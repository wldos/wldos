/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.enums;

/**
 * 用户状态枚举。
 * 当开启邮箱激活时，未激活的用户账号处于未激活状态，登录时自动跳转到激活页面。
 * 激活后，更新状态为正常。
 *
 * @author 树悉猿
 * @date 2022/9/5
 * @version 1.0
 */
public enum UserStatusEnum {

	/** 未激活的,见习用户。*/
	notActive("未激活的", "notActive"),
	/** 已锁定 */
	locked("已锁定", "locked"),
	/** 已注销 */
	cancelled("cancelled", "cancelled"),
	/** 正常 */
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
