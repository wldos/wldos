/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

import java.util.List;

/**
 * 面包屑数据，tdk数据。
 *
 * @author 树悉猿
 * @date 2021/8/29
 * @version 1.0
 */
public class SeoCrumbs {
	private String title;

	private String description;

	private String keywords;

	private List<Breadcrumb> crumbs;

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
}
