/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "评论信息")
@JsonIgnoreProperties({ "authorEmail", "authorUrl" })
@Getter
@Setter
public class Comment extends TreeNode<Comment> {

	@ApiModelProperty(value = "作者名称", example = "用户名")
	private String author;

	@ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
	private String avatar;

	@ApiModelProperty(value = "评论内容", example = "评论内容...")
	private String content;

	@ApiModelProperty(value = "评论时间", example = "2023-01-01 00:00:00")
	private Timestamp datetime;

	@ApiModelProperty(value = "作者邮箱", hidden = true)
	private String authorEmail;

	@ApiModelProperty(value = "作者URL", hidden = true)
	private String authorUrl;
}
