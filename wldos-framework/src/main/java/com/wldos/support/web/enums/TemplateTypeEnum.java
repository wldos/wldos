/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.web.enums;

/**
 * 模板类型枚举值。模板是指为了实现特定业务模型而实现的功能，偏指前端表现层，如：商品详情页面、电子书目录页面。
 * 注意：模板不是模型，一个模板可能支持多个模型，模板是业务模型在技术层面上的投影（浓缩的物化实现）。
 * 业务模型是业务类型的模型化抽象，不同的业务类型可能具备类似或相同的业务模型。
 * 业务类型，是对业务的划分。业务，是对现实世界中密切关联的事物活动的通称。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum TemplateTypeEnum {
	PRODUCT("产品", "product"),
	ARCHIVES("存档", "archives"),
	CATEGORY("类目", "category"),
	INFO("信息", "info"),
	ADMIN("系统管理", "admin"),
	STATIC("静态", "static"),
	UNKNOWN("未知", "unknown");

	private final String label;

	private final String value;

	TemplateTypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public static TemplateTypeEnum getTemplateTypeEnumByValue(String value) {
		for (TemplateTypeEnum t : TemplateTypeEnum.values()) {
			if (t.getValue().equals(value))
				return t;
		}
		return TemplateTypeEnum.UNKNOWN;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}