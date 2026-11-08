/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import io.github.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 授权资源树节点。
 *
 * @author Yuanxi Universe
 * @date 2021/5/21
 * @version 1.0
 */
@ApiModel(description = "授权资源树节点")
@Getter
@Setter
public class AuthRes extends TreeNode<AuthRes> {
	@ApiModelProperty(value = "资源标题", example = "用户管理")
	private String title;

	@ApiModelProperty(value = "资源键值", example = "1")
	private String key;

	@ApiModelProperty(value = "是否禁用", example = "false")
	private boolean disabled;

	@ApiModelProperty(value = "资源编码", example = "adminMenu")
	private String resourceCode;

	@ApiModelProperty(value = "资源路径（鉴权路由）", example = "/admin/sys/user/adminMenu")
	private String resourcePath;

	@ApiModelProperty(value = "请求方法", example = "GET")
	private String requestMethod;

	@ApiModelProperty(value = "资源类型 wo_resource.resource_type", example = "admin_menu")
	private String resourceType;

	@ApiModelProperty(value = "菜单节点形态 menu_node_kind", example = "nav_dir")
	private String menuNodeKind;

	public AuthRes() {
	}

	private AuthRes(String title, String key, Long id, Long parentId, Long displayOrder) {
		this.title = title;
		this.key = key;
		this.id = id;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
	}

	public static AuthRes of(String title, String key, Long id, Long parentId, Long displayOrder) {
		return new AuthRes(title, key, id, parentId, displayOrder);
	}

	@Override
	public String toString() {
		return "{title: " + title + ", key: " + key + ", children: " + this.children + ", disabled: " + disabled + "}";
	}
}
