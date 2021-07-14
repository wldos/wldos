/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.vo;


import java.io.Serializable;

import com.wldos.support.vo.TreeNode;

/**
 * 符合父子结构的菜单。
 *
 * @Title Menu
 * @Package com.wldos.system.auth.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/30
 * @Version 1.0
 */
public class Menu extends TreeNode<Menu> implements Serializable {
	/** 菜单URI */
	private String path;
	/** 菜单icon */
	private String icon;
	/** 菜单显示名称 */
	private String name;
	/** 菜单打开方式 */
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
