/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.enums;

import com.wldos.support.issue.IssueConstants;

/**
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum IssueVersionEnum {

	Free("社区版", IssueConstants.DEFAULT_VERSION),
	Standard("标准版", "Standard"),
	Ultimate("旗舰版", "Ultimate");

	private final String label;
	private final String value;

	IssueVersionEnum(String label, String value) {
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