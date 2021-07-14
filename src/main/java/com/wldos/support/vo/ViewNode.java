/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.vo;

/**
 * 视图树节点。
 *
 * @Title ViewNode
 * @Package com.wldos.cms.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/6/17
 * @Version 1.0
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
