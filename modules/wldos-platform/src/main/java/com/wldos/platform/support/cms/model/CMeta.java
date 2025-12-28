/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.model;

import java.util.List;

import com.wldos.platform.support.cms.dto.PubTypeExt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Pub元模型定义。
 *
 * @author 元悉宇宙
 * @date 2021/6/19
 * @version 1.0
 */
@ApiModel(description = "Pub元模型定义")
@Getter
@Setter
public class CMeta {

	@ApiModelProperty(value = "封面特色图路径", example = "/images/cover.jpg")
	protected String cover;

	@ApiModelProperty(value = "查看数", example = "1000")
	protected String views;

	@ApiModelProperty(value = "水印文本", example = "WLDOS")
	protected String watermarkText;

	@ApiModelProperty(value = "是否启用水印：1=启用, 0=禁用", example = "1")
	protected String watermarkEnabled;

	@ApiModelProperty(value = "发布类型扩展属性列表")
	protected List<PubTypeExt> pubTypeExt;
}
