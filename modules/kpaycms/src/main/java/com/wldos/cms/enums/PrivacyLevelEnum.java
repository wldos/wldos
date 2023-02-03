/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.enums;

/**
 * 隐私级别枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum PrivacyLevelEnum {
	/** 公开 */
	PUB("公开查看", "public"),
	/** 查看码可见 */
	TOKEN("查看码可见", "token"),
	/** 打赏可见 */
	REWARD("打赏可见", "reward");

	private final String label;

	private final String value;

	PrivacyLevelEnum(String label, String value) {
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
