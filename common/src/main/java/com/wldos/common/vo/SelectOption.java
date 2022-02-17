/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
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

	public SelectOption(String label, String value) {
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
