/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.model;

import java.util.List;

import com.wldos.sys.base.dto.ContentExt;

/**
 * 元模型定义。
 *
 * @author 树悉猿
 * @date 2021/6/19
 * @version 1.0
 */
public class CMeta {

	/** 封面特色图path */
	protected String cover;

	/** 查看数 */
	protected String views;

	protected List<ContentExt> contentExt;

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public List<ContentExt> getContentExt() {
		return contentExt;
	}

	public void setContentExt(List<ContentExt> contentExt) {
		this.contentExt = contentExt;
	}
}
