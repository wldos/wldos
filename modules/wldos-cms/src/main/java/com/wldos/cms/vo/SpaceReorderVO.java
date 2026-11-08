/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 创作工作台同级内容排序请求。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "创作工作台排序")
@Getter
@Setter
public class SpaceReorderVO {

	@ApiModelProperty(value = "按目标顺序排列的内容 id 列表", required = true)
	private List<Long> orderedIds;

	@ApiModelProperty(value = "与作品列表 filter.pubType 一致的类型筛选，空则不限")
	private List<String> pubTypes;

	@ApiModelProperty(value = "与当前列表 keyword 一致，空则不限")
	private String keyword;
}
