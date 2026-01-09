/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import com.wldos.cms.entity.KPubs;
import com.wldos.common.utils.ObjectUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 作品的章节元素，可以派生出剧集。
 *
 * @author 元悉宇宙
 * @date 2021/6/22
 * @version 1.0
 */
@ApiModel(description = "作品章节信息")
@Getter
@Setter
public class Chapter {
	@ApiModelProperty(value = "章节ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "章节标题")
	private String pubTitle;

	@ApiModelProperty(value = "内容正文", example = "章节内容...")
	private String pubContent;

	@ApiModelProperty(value = "MIME类型", example = "text/html")
	private String pubMimeType;

	@ApiModelProperty(value = "父级ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "发布状态", example = "PUBLISH")
	private String pubStatus;

	public Chapter() {
	}

	public static Chapter of(KPubs single) {
		return of(single.getId(), ObjectUtils.string(single.getPubTitle()), ObjectUtils.string(single.getPubContent())/* 过滤null值，防止前端不刷新*/,
				single.getPubMimeType(), single.getParentId(), single.getPubStatus());
	}

	public static Chapter of(Long id, String pubTitle, String pubContent, String pubMimeType, Long parentId, String pubStatus) {
		return new Chapter(id, pubTitle, pubContent, pubMimeType, parentId, pubStatus);
	}

	private Chapter(Long id, String pubTitle, String pubContent, String pubMimeType, Long parentId, String pubStatus) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.pubContent = pubContent;
		this.pubMimeType = pubMimeType;
		this.parentId = parentId;
		this.pubStatus = pubStatus;
	}
}
