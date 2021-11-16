/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.system.auth.dto;

import java.util.List;
import java.util.Map;

import com.wldos.system.auth.vo.Menu;
import com.wldos.system.auth.vo.Route;

/**
 * 菜单和路由。
 *
 * @author 树悉猿
 * @date 2021/9/8
 * @version 1.0
 */
public class MenuAndRoute {
	private List<Menu> menu;
	private Map<String, Route> route;

	public MenuAndRoute(){}

	public MenuAndRoute(List<Menu> menu, Map<String, Route> route) {
		this.menu = menu;
		this.route = route;
	}

	public List<Menu> getMenu() {
		return menu;
	}

	public void setMenu(List<Menu> menu) {
		this.menu = menu;
	}

	public Map<String, Route> getRoute() {
		return route;
	}

	public void setRoute(Map<String, Route> route) {
		this.route = route;
	}
}
