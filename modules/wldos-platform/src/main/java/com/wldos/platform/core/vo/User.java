/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;
import java.util.Map;

import com.wldos.framework.support.auth.vo.Token;
import com.wldos.platform.support.auth.vo.UserInfo;
import com.wldos.platform.support.resource.vo.Menu;
import com.wldos.platform.support.resource.vo.Route;

/**
 * 登录后返回用户信息。
 *
 * @author 元悉宇宙
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