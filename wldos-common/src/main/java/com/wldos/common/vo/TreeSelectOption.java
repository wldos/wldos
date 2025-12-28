/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

/**
 * 树形下拉列表节点。用于生成TreeSelect控件数据。
 *
 * @author 元悉宇宙
 * @date 2021/12/16
 * @version 1.0
 */
@JsonIgnoreProperties({ "id", "parentId", "displayOrder" })
@Getter
public class TreeSelectOption extends TreeNode<TreeSelectOption> {
	private String title;

	private String value;

	private String key;

	public TreeSelectOption() {
	}

	private TreeSelectOption(Long id, Long parentId, String title, String value, String key, Long displayOrder) {
		super(id, parentId, displayOrder);
		this.title = title;
		this.value = value;
		this.key = key;
	}

	public static TreeSelectOption of(Long id, Long parentId, String title, String value, String key, Long displayOrder) {
		return new TreeSelectOption(id, parentId, title, value, key, displayOrder);
	}

	public String toString() {
		return "{title: " + title + ", value: " + value + ", key: " + key + ", children: " + this.children + "}";
	}
}