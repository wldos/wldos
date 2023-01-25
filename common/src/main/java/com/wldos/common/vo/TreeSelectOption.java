/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 树形下拉列表节点。用于生成TreeSelect控件数据。
 *
 * @author 树悉猿
 * @date 2021/12/16
 * @version 1.0
 */
@JsonIgnoreProperties({"id", "parentId"})
public class TreeSelectOption extends TreeNode<TreeSelectOption> {
	private String title;
	private String value;
	private String key;

	public TreeSelectOption() {
	}

	private TreeSelectOption(Long id, Long parentId, String title, String value, String key) {
		super(id, parentId);
		this.title = title;
		this.value = value;
		this.key = key;
	}

	public static TreeSelectOption of (Long id, Long parentId, String title, String value, String key) {
		return new TreeSelectOption(id, parentId, title, value, key);
	}

	public String getTitle() {
		return title;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		return "{title: " + title + ", value: " + value + ", key: " + key + ", children: " + this.children + "}";
	}
}