/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.resource.vo;


import java.io.Serializable;

import com.wldos.common.vo.TreeNode;

/**
 * 符合父子结构的菜单。遵循ProLayout菜单数据模板。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
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

	private Long displayOrder;

	/* 在菜单中隐藏子节点
	hideChildrenInMenu?: boolean;*/
	/* 在菜单中隐藏自己和子节点
	hideInMenu?: boolean;*/
	/* 在面包屑中隐藏
	hideInBreadcrumb?: boolean;*/
	/* 自定义菜单的国际化 key
	locale?: string | false;*/
	/* 用于标定选中的值，默认是 path
	key?: string;*/
	/* disable 菜单选项
	disabled?: boolean;*/
	/*
	 * @deprecated 当此节点被选中的时候也会选中 parentKeys 的节点
	 * 自定义父节点
	parentKeys?: string[]; */
	/* 隐藏自己，并且将子节点提升到与自己平级
	flatMenu?: boolean;*/

	public Menu() {
		super();
	}

	private Menu(String path, String icon, String name, String target, Long id, Long parentId, Long displayOrder) {
		this.path = path;
		this.icon = icon;
		this.name = name;
		this.target = target;
		this.id = id;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
	}

	public static Menu of(String path, String icon, String name, String target, Long id, Long parentId, Long displayOrder) {
		return new Menu(path, icon, name, target, id, parentId, displayOrder);
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

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String toString() {
		return "{name: " + this.name + ", path: " + this.path + ", icon: " + this.icon + ", target: " + this.target + "}";
	}
}
