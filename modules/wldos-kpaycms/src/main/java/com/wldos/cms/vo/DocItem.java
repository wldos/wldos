/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 文档导航项。
 *
 * @author 元悉宇宙
 * @date 2023/5/13
 * @version 1.0
 */
@ApiModel(description = "文档导航项")
@Getter
@Setter
public class DocItem {

	@ApiModelProperty(value = "文档项ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "文档项标题")
	private String pubTitle;
}
