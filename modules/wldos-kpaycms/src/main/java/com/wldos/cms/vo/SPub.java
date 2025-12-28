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
 * 全文检索信息。
 *
 * @author 元悉宇宙
 * @date 2021/12/03
 * @version 1.0
 */
@ApiModel(description = "全文检索信息")
@Getter
@Setter
public class SPub {
	@ApiModelProperty(value = "内容ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "内容标题")
	private String pubTitle;

	@ApiModelProperty(value = "发布类型", example = "POST")
	private String pubType;
}
