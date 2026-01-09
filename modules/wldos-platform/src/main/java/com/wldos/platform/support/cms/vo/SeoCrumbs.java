/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 面包屑数据，tdk数据。
 *
 * @author 元悉宇宙
 * @date 2021/8/29
 * @version 1.0
 */
@ApiModel(description = "面包屑数据，TDK数据（Title、Description、Keywords）")
@Getter
@Setter
public class SeoCrumbs {
	@ApiModelProperty(value = "页面标题", example = "示例页面标题")
	private String title;

	@ApiModelProperty(value = "页面描述", example = "这是一个示例页面")
	private String description;

	@ApiModelProperty(value = "页面关键词", example = "示例,关键词,SEO")
	private String keywords;

	@ApiModelProperty(value = "面包屑列表")
	private List<Breadcrumb> crumbs;

	@ApiModelProperty(value = "发布类型，用于框架级分类处理", example = "product")
	private String pubType;

	public SeoCrumbs() {
	}

	public static SeoCrumbs of(String title, String description, String keywords, List<Breadcrumb> crumbs) {
		return new SeoCrumbs(title, description, keywords, crumbs);
	}

	public static SeoCrumbs of(String title, String description, String keywords, String pubType) {
		return new SeoCrumbs(title, description, keywords, pubType);
	}

	private SeoCrumbs(String title, String description, String keywords, List<Breadcrumb> crumbs) {
		this.title = title;
		this.description = description;
		this.keywords = keywords;
		this.crumbs = crumbs;
	}

	private SeoCrumbs(String title, String description, String keywords, String pubType) {
		this.title = title;
		this.description = description;
		this.keywords = keywords;
		this.pubType = pubType;
	}
}
