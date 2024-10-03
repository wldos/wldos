/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.common.vo;

/**
 * 值枚举，用于前端字典值翻译，只适用于数量较小的枚举类。
 *
 * @author 元悉宇宙
 * @date 2021/12/18
 * @version 1.0
 */
public class ValueEnum {
	private String text;

	private String value;

	public ValueEnum() {
	}

	public ValueEnum(String text, String value) {
		this.text = text;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}