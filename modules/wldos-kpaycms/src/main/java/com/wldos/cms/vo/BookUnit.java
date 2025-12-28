/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 作品列表单元信息。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@ApiModel(description = "作品列表单元信息")
@Getter
@Setter
public class BookUnit {
	@ApiModelProperty(value = "作品ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "作品标题")
	private String pubTitle;

	@ApiModelProperty(value = "副标题", example = "作品副标题")
	private String subTitle; // 扩展属性：副标题

	@ApiModelProperty(value = "封面URL", example = "https://example.com/cover.jpg")
	private String cover; // 扩展属性：封面url

	@ApiModelProperty(value = "创建时间", example = "2023-01-01 00:00:00")
	private Timestamp createTime;

	@ApiModelProperty(value = "创建人ID", example = "1")
	private Long createBy;

	@ApiModelProperty(value = "成员列表")
	private List<PubMember> members;

}
