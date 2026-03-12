/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.util.List;

import com.wldos.platform.core.enums.PubTypeEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 作品，以电子书为蓝本代表所有类型的作品，必要的时候再细化。
 *
 * @author 元悉宇宙
 * @date 2021/6/22
 * @version 1.0
 */
@ApiModel(description = "作品信息")
@Getter
@Setter
public class Book {
	@ApiModelProperty(value = "作品ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "作品标题")
	private String pubTitle;

	@ApiModelProperty(value = "发布类型", example = "BOOK")
	private String pubType;

	/** 是否单体类型：'1'是，'0'否*/
	@ApiModelProperty(value = "是否单体类型：true=是，false=否", example = "false")
	private Boolean isSingle;

	@ApiModelProperty(value = "发布状态", example = "PUBLISH")
	private String pubStatus;

	@ApiModelProperty(value = "章节列表")
	private List<Chapter> chapter;

	public Book() {
	}

	public static Book of(Long id, String pubTitle, String pubType, String pubStatus, List<Chapter> chapters) {
		return new Book(id, pubTitle, pubType, PubTypeEnum.isSingle(pubType), pubStatus, chapters);
	}

	private Book(Long id, String pubTitle, String pubType, Boolean isSingle, String pubStatus, List<Chapter> chapter) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.pubType = pubType;
		this.isSingle = isSingle;
		this.pubStatus = pubStatus;
		this.chapter = chapter;
	}
}
