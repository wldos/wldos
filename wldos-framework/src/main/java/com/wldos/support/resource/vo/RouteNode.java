/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.resource.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由节点。
 *
 * @author 树悉猿
 * @date 2023/4/5
 * @version 1.0
 */
public class RouteNode<T> {
	protected String path;

	protected String component;

	protected String redirect;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public List<T> getRoutes() {
		return routes;
	}

	public void setRoutes(List<T> routes) {
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