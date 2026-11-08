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
 * 行政区划搜索命中路径（省 / 市 / 区县）。
 */
@ApiModel(description = "行政区划搜索路径")
@Getter
@Setter
public class RegionSearchPathVO {

	@ApiModelProperty(value = "路径 id 列表（wo_region.id，省→市→区县）")
	private List<Long> ids = new ArrayList<>();

	@ApiModelProperty(value = "路径名称列表，与 ids 一一对应")
	private List<String> labels = new ArrayList<>();

	public static RegionSearchPathVO of(List<Long> ids, List<String> labels) {
		RegionSearchPathVO vo = new RegionSearchPathVO();
		vo.setIds(ids);
		vo.setLabels(labels);
		return vo;
	}
}
