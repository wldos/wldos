/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.vo;

/**
 * 视图树节点(树形独立组件的节点数据，不用于表单)。
 *
 * @author 树悉猿
 * @date 2021/6/17
 * @version 1.0
 */
public class ViewNode extends TreeNode<ViewNode> {
	private String title;

	private String key;

	private boolean disabled;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String toString() {
		return "{title: " + title.toString() + ", key: " + key.toString() + ", children: " + this.children + ", disabled: " + disabled + "}";
	}
}