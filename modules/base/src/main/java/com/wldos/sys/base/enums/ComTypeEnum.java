/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * @author 树悉猿
 * @date 2021/11/29
 * @version 1.0
 */
public enum ComTypeEnum {
	ENTERPRISE("企业", "1"),
	COMPANY("公司", "11"),
	NON_COMPANY("非公司制企业法人", "13"),
	BRANCH_OFFICE("企业分支机构", "15"),
	PARTNERSHIP("个独、合伙企业", "17"),
	OTHER_ENTER("其他企业", "19"),
	OFFICE("机关", "3"),
	PARTY("中国共产党", "31");

	private final String label;

	private final String value;

	ComTypeEnum(String label, String value) {
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