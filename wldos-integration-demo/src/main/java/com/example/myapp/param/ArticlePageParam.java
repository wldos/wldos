/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.param;

import io.github.wldos.platform.support.mp.query.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分页查询入参（≈ 第三方 {@code XxxParam extends BasePageParam}）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "文章分页查询")
public class ArticlePageParam extends PageParam {

	@ApiModelProperty(value = "作者", example = "张三")
	private String author;

	@ApiModelProperty(value = "状态(0草稿/1已发布/2已删除)", example = "1")
	private Integer status;

	@ApiModelProperty(value = "标题关键词", example = "Spring")
	private String keyword;

	@ApiModelProperty(hidden = true)
	private String internalScopeHint;
}
