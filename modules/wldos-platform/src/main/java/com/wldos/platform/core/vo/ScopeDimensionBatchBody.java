/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量登记业务字段（每种字典类型一行目录）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "批量登记业务字段请求体")
@Getter
@Setter
public class ScopeDimensionBatchBody {

	@ApiModelProperty(value = "字典编码列表（class_type 或 DictRegistry code）", required = true)
	private List<String> dictCodes;

	@ApiModelProperty(value = "dimension_code 后缀，默认 _code；最终编码={dictCode}{suffix}", example = "_code")
	private String codeSuffix;

	@ApiModelProperty(value = "是否启用：1=是，0=否", example = "1")
	private String enabled;

	@ApiModelProperty(value = "字段范围：global=全局字段，extension=定制字段", example = "global")
	private String scopeUiTier;
}
