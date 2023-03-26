/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
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
@JsonIgnoreProperties({ "id", "parentId", "displayOrder" })
public class Category extends TreeNode<Category> {

	private String title;

	private String key;

	private String slug;

	public Category() {
	}

	public static Category of(Long id, Long parentId, String title, String key, String slug, Long displayOrder) {
		return new Category(id, parentId, title, key, slug, displayOrder);
	}

	private Category(Long id, Long parentId, String title, String key, String slug, Long displayOrder) {
		super(id, parentId, displayOrder);
		this.title = title;
		this.key = key;
		this.slug = slug;
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
}
