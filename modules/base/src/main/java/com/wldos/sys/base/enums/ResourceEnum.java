/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 资源相关枚举值。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum ResourceEnum {
	/** 菜单类 */
	MENU("菜单", "menu"),
	/** 操作类 */
	BUTTON("操作", "button"),
	/** 接口服务 */
	WEB_SERV("服务", "webServ"),
	/** 数据服务 */
	DATA_SERV("数据", "dataServ"),
	/** 静态资源 */
	STATIC("静态", "_static"),
	/** 管理菜单 */
	ADMIN_MENU("管理菜单", "admin_menu"),
	OTHER("其他", "other");

	private final String label;

	private final String value;

	ResourceEnum(String label, String value) {
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
