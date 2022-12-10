/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.enums;

/**
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum IssueModuleEnum {
	Framework("基础框架", "Base"),
	Platform("支撑平台", "Login"),
	KPayCMS("内容付费", "Cms");

	private final String label;

	private final String value;

	IssueModuleEnum(String label, String value) {
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