/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 分类法枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum TermTypeEnum {
	/** 分类目录 */
	CATEGORY("category"),
	/** 标签 */
	TAG("tag");

	private final String name;

	TermTypeEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
