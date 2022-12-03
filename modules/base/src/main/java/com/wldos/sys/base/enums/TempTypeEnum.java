/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 模板类型枚举值。不同的模板可以展示不同信息格式(包括：页面内容、seo信息等)，用于设置动态模板的模板类型。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum TempTypeEnum {
	/** 产品模板，用来展示产品(作品、服务、软件、实物等都是产品)集合，从产品的角度展现 */
	PRODUCT("产品", "product"),
	/** 存档模板，用来展示存档信息(作品内容、产品、信息的存档)集合，从内容的角度展现 */
	ARCHIVES("存档", "archives"),
	/** 类目模板，用来展示分类目录，从分类的角度展现 */
	CATEGORY("类目", "category"),
	/** 信息模板，用来展示供求信息(买卖双方在各领域的供求信息)集合，从信息要素的角度展现 */
	INFO("信息", "info"),
	/** 静态模板，标识非动态模板的独立页面 */
	STATIC("静态", "static"),
	/** 未知类型模板，用来展示未知内容模板 */
	UNKNOWN("未知", "unknown");

	private final String label;

	private final String value;

	TempTypeEnum(String label, String value) {
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
