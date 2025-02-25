/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.vo;

import java.util.List;

/**
 * 面包屑数据，tdk数据。
 *
 * @author 元悉宇宙
 * @date 2021/8/29
 * @version 1.0
 */
public class SeoCrumbs {
	private String title;

	private String description;

	private String keywords;

	private List<Breadcrumb> crumbs;

	private String pubType; // 用于框架级分类处理

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public List<Breadcrumb> getCrumbs() {
		return crumbs;
	}

	public void setCrumbs(List<Breadcrumb> crumbs) {
		this.crumbs = crumbs;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}
}
