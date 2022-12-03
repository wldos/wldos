/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wldos.common.vo.TreeNode;

/**
 * 分类目录信息。
 *
 * @author 树悉猿
 * @date 2022/1/1
 * @version 1.0
 */
@JsonIgnoreProperties({"id", "parentId"})
public class Category extends TreeNode<Category> {

	private String title;

	private String key;

	private String slug;

	private String conType;

	public Category() {
	}

	public Category(Long id, Long parentId, String title, String key, String slug, String conType) {
		super(id, parentId);
		this.title = title;
		this.key = key;
		this.slug = slug;
		this.conType = conType;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	public String getSlug() {
		return slug;
	}

	public String getConType() {
		return conType;
	}
}
