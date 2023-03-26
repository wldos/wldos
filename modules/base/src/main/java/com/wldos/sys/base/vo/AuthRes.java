/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.vo;

import com.wldos.common.vo.TreeNode;

/**
 * 授权资源树节点。
 *
 * @author 树悉猿
 * @date 2021/5/21
 * @version 1.0
 */
public class AuthRes extends TreeNode<AuthRes> {
	private String title;

	private String key;

	private boolean disabled;

	public AuthRes() {
	}

	private AuthRes(String title, String key, Long id, Long parentId) {
		this.title = title;
		this.key = key;
		this.id = id;
		this.parentId = parentId;
	}

	public static AuthRes of(String title, String key, Long id, Long parentId) {
		return new AuthRes(title, key, id, parentId);
	}

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
		return "{title: " + title + ", key: " + key + ", children: " + this.children + ", disabled: " + disabled + "}";
	}
}
