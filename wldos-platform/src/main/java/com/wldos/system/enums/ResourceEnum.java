/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 资源相关枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum ResourceEnum {

	MENU("menu"),

	BUTTON("button"),

	WEB_SERV("webServ"),

	DATA_SERV("dataServ"),

	STATIC("_static"),

	ADMIN_MENU("admin_menu"),
	OTHER("other");

	private final String resName;

	ResourceEnum(String resName) {
		this.resName = resName;
	}

	@Override
	public String toString() {
		return this.resName;
	}
}
