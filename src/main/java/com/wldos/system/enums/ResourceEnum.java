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
	/**	菜单类 */
	MENU("menu"),
	/** 操作类 */
	BUTTON("button"),
	/** 接口服务 */
	WEB_SERV("webServ"),
	/** 数据服务 */
	DATA_SERV("dataServ"),
	/** 静态资源 */
	STATIC("_static"),
	/** 管理菜单 */
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
