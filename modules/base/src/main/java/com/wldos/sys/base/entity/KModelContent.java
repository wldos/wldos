/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 业务模型管理的行业门类：用来定义业务对象归属业务场景的大类，基于行业门类或专业门类划分，便于在此场景内展开业务分类管理，行业门类可以细分成
 * 分类目录，比如文化体育娱乐门类包含了艺术音乐，而音乐包含了各种乐谱（各种乐器谱、简谱等）。
 */
@Table
public class KModelContent {

	@Id
	private Long id;

	private String contentName;

	private String contentCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getContentCode() {
		return contentCode;
	}

	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}
}
