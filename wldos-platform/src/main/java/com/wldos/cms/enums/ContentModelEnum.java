/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.enums;

/**
 * 内容元模型枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum ContentModelEnum {

	ARTICLE("article"),

	COLLECT("collect"),

	FILE("file"),

	MULTIMEDIA("multimedia");

	private final String name;

	ContentModelEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
