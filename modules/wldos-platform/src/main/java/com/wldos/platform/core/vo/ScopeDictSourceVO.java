/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 可登记为业务字段的枚举类型字典源。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "业务字段可选字典源")
@Getter
@Setter
public class ScopeDictSourceVO {

	@ApiModelProperty(value = "字典编码（登记为 DICT:{dictCode}）", example = "brand")
	private String dictCode;

	@ApiModelProperty(value = "展示名称", example = "品牌")
	private String name;

	@ApiModelProperty(value = "来源：ENUM=Java枚举注册，TERM=类型管理 class_type", example = "TERM")
	private String sourceKind;

	@ApiModelProperty(value = "是否已在业务字段登记", example = "false")
	private Boolean registered;

	@ApiModelProperty(value = "已登记时的 dimension_code", example = "brand_code")
	private String registeredDimensionCode;
}
