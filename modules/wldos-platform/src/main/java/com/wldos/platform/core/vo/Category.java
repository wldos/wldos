/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;

/**
 * 分类目录信息。
 *
 * @author 元悉宇宙
 * @date 2022/1/1
 * @version 1.0
 */
@ApiModel(description = "分类目录信息")
@JsonIgnoreProperties({ "id", "parentId", "displayOrder" })
@Getter
public class Category extends TreeNode<Category> {

	@ApiModelProperty(value = "分类标题", example = "技术分类")
	private String title;

	@ApiModelProperty(value = "分类键值", example = "tech")
	private String key;

	@ApiModelProperty(value = "分类别名", example = "technology")
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
}
