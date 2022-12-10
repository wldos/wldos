/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.vo;

import java.util.List;
import java.util.Map;

import com.wldos.sys.base.vo.Menu;
import com.wldos.sys.base.vo.Route;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.auth.vo.UserInfo;

/**
 * 登录后返回用户信息。
 *
 * @author 树悉猿
 * @date 2021-04-30
 * @version V1.0
 */
@SuppressWarnings("unused")
public class User {
	private UserInfo userInfo;
	private List<Menu> menu;
	private Map<String, Route> route;
	private List<String> currentAuthority;
	private Token token;
	private int isManageSide = 0;


	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
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

	public List<String> getCurrentAuthority() {
		return currentAuthority;
	}

	public void setCurrentAuthority(List<String> currentAuthority) {
		this.currentAuthority = currentAuthority;
	}

	public int getIsManageSide() {
		return isManageSide;
	}

	public void setIsManageSide(int isManageSide) {
		this.isManageSide = isManageSide;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
}