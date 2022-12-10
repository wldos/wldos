/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum ContModelTypeEnum {
	POST("post"),
	PAGE("page"),
	BOOK("book"),
	CHAPTER("chapter"),
	PRODUCT("prod"),
	SERVICE("service"),
	TASK("task"),
	INFO("info"),
	ATTACHMENT("attachment"),
	NAV_ITEM_MENU("navItemMenu");

	private final String name;

	ContModelTypeEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}