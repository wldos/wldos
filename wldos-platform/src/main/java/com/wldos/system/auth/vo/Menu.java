/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.vo;


import java.io.Serializable;

import com.wldos.support.vo.TreeNode;

/**
 * 符合父子结构的菜单。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
public class Menu extends TreeNode<Menu> implements Serializable {

	private String path;

	private String icon;

	private String name;

	private String target;

	public Menu() {
		super();
	}

	public Menu(String path, String icon, String name) {
		this();
		this.path = path;
		this.icon = icon;
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String toString() {
		return "{name: " + this.name + ", path: " + this.path + ", icon: " + this.icon + ", target: " + this.target + "}";
	}
}
