/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
 * 文档，一个文档可以有章节。
 *
 * @author 元悉宇宙
 * @date 2023/5/14
 * @version 1.0
 */
@ApiModel(description = "文档信息")
@Getter
@Setter
public class Doc {
	@ApiModelProperty(value = "文档ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "文档标题")
	private String pubTitle;

	@ApiModelProperty(value = "章节列表")
	private List<DocItem> chapter;

	public Doc() {
	}

	public static Doc of(Long id, String pubTitle, List<DocItem> chapters) {
		return new Doc(id, pubTitle, chapters);
	}

	private Doc(Long id, String pubTitle, List<DocItem> chapter) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.chapter = chapter;
	}
}
