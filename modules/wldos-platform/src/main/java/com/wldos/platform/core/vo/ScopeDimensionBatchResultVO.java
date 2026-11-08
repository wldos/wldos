/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量登记业务字段结果。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "批量登记业务字段结果")
@Getter
@Setter
public class ScopeDimensionBatchResultVO {

	@ApiModelProperty(value = "成功新建条数")
	private int createdCount;

	@ApiModelProperty(value = "跳过条数")
	private int skippedCount;

	@ApiModelProperty(value = "已新建的 dimension_code 列表")
	private List<String> createdDimensionCodes = new ArrayList<>();

	@ApiModelProperty(value = "跳过项")
	private List<ScopeDimensionBatchSkipVO> skipped = new ArrayList<>();
}
