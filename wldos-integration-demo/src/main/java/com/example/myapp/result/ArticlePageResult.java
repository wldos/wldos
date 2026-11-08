/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 文章分页行（列表接口出参，不直接暴露实体）。
 */
@Data
@ApiModel(description = "文章分页行")
public class ArticlePageResult {

	@ApiModelProperty(value = "主键")
	private Long id;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "作者")
	private String author;

	@ApiModelProperty(value = "浏览量")
	private Integer views;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "创建时间")
	private Timestamp createTime;
}
