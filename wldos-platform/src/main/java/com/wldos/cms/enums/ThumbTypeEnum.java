/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.enums;

/**
 * 缩略图类型枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum ThumbTypeEnum {
	
	THUMBNAIL("缩略图", "thumbnail"),
	
	MEDIUM("中等", "medium"),
	
	MEDIUM_LARGE("中大", "mediumLarge"),
	
	LARGE("大", "large"),
	
	HUGE("超大", "huge");

	private final String label;
	private final String value;

	ThumbTypeEnum(String label, String value) {
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
		return "{label: '"+this.label+"', value: '"+this.value+"'}";
	}
}
