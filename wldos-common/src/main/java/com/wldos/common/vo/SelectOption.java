/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.vo;

/**
 * 下拉列表option。用于生成简单的下拉列表数据。
 *
 * @author 树悉猿
 * @date 2021/12/18
 * @version 1.0
 */
public class SelectOption {
	private String label;

	private String value;

	public SelectOption() {
	}

	public static SelectOption of(String label, String value) {
		return new SelectOption(label, value);
	}

	private SelectOption(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}