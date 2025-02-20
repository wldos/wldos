/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wldos.common.vo.TreeNode;

/**
 * 分类目录信息。
 *
 * @author 元悉宇宙
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
