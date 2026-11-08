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
 * 创作工作台单条内容上移/下移。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "创作工作台顺序微调")
@Getter
@Setter
public class SpaceShiftVO {

	@ApiModelProperty(value = "内容 id", required = true)
	private Long id;

	@ApiModelProperty(value = "步长：-1 上移，1 下移", required = true, example = "-1")
	private Integer step;

	@ApiModelProperty(value = "作品列表 filter.pubType，空则不限")
	private List<String> pubTypes;

	@ApiModelProperty(value = "与当前列表 keyword 一致，空则不限")
	private String keyword;
}
