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
 * 创作工作台列表与排序共用的筛选范围（须与当前列表查询一致）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "创作工作台列表筛选范围")
@Getter
@Setter
public class WorkbenchListScope {

	@ApiModelProperty(value = "与作品列表 filter.pubType 一致，空则不限")
	private List<String> pubTypes;

	@ApiModelProperty(value = "与作品/章节列表 keyword 一致，空则不限")
	private String keyword;
}
