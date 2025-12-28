/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.resource.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 路由节点。
 *
 * @author 元悉宇宙
 * @date 2023/4/5
 * @version 1.0
 */
@ApiModel(description = "路由节点")
@Getter
@Setter
public class RouteNode<T> {
	@ApiModelProperty(value = "路由路径", example = "/admin/sys/user")
	protected String path;

	@ApiModelProperty(value = "组件路径", example = "/admin/sys/user/index")
	protected String component;

	@ApiModelProperty(value = "重定向路径", example = "/admin/sys/user/list")
	protected String redirect;

	@ApiModelProperty(value = "子路由列表")
	protected List<T> routes = null;

	public RouteNode() {
	}

	public RouteNode(String component) {
		this.component = component;
	}

	public RouteNode(String path, String component) {
		this(component);
		this.path = path;
	}

	public RouteNode(String path, String component, String redirect) {
		this(path, component);
		this.redirect = redirect;
	}

	public RouteNode(String path, String component, String redirect, List<T> routes) {
		this(path, component, redirect);
		this.routes = routes;
	}

	public void add(T node) {
		if (this.routes == null) {
			this.routes = new ArrayList<T>();
		}
		this.routes.add(node);
	}

	public void addAll(List<T> nodes) {
		if (this.routes == null) {
			this.routes = new ArrayList<T>();
		}
		this.routes.addAll(nodes);
	}
}