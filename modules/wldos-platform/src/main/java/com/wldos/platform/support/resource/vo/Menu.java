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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 符合父子结构的菜单。遵循ProLayout菜单数据模板。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
@ApiModel(description = "菜单信息，符合父子结构，遵循ProLayout菜单数据模板")
@Getter
@Setter
public class Menu extends TreeNode<Menu> implements Serializable {
	@ApiModelProperty(value = "菜单类型，参考ResourceEnum枚举", example = "menu")
	private String type;

	@ApiModelProperty(value = "菜单URI路径", example = "/admin/sys/user")
	private String path;

	@ApiModelProperty(value = "组件路径", example = "/admin/sys/user/index")
	private String component;

	@ApiModelProperty(value = "菜单图标", example = "user")
	private String icon;

	@ApiModelProperty(value = "菜单显示名称", example = "用户管理")
	private String name;

	@ApiModelProperty(value = "菜单打开方式", example = "_self")
	private String target;

	@ApiModelProperty(value = "显示顺序", example = "1")
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

	private Menu(String type, String path, String comPath, String icon, String name, String target, Long id, Long parentId, Long displayOrder) {
		this.type = type;
		this.path = path;
		this.component = comPath;
		this.icon = icon;
		this.name = name;
		this.target = target;
		this.id = id;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
	}

	public static Menu of(String type, String path, String comPath, String icon, String name, String target, Long id, Long parentId, Long displayOrder) {
		return new Menu(type, path, comPath, icon, name, target, id, parentId, displayOrder);
	}

	public String toString() {
		return "{name: " + this.name + ", path: " + this.path + ", type: " + this.type + ", icon: " + this.icon + ", target: " + this.target + "}";
	}
}
