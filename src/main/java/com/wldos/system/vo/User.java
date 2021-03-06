/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.vo;

import java.util.List;

import com.wldos.system.auth.vo.Menu;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.auth.vo.UserInfo;

/**
 * 登录后返回用户信息。
 *
 * @author 树悉猿
 * @date 2021-04-30
 * @version V1.0
 */
public class User {
	/** 用户信息 */
	private UserInfo userInfo;
	/** 用户授权的菜单 */
	private List<Menu> menu;
	/** 用户的权限：默认排除菜单权限 */
	private List<String> currentAuthority;

	private Token token;

	/** 是否管理端 */
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

/*
{
"userName": "MenuManager",
    "userRoles": [
      "R_MENUADMIN"
    ],
    "userPermissions": [
      "p_menu_view",
      "p_menu_edit",
      "p_menu_menu"
    ],
    "accessMenus": [
      {
        "title": "系统",
        "path": "/system",
        "icon": "cogs",
        "children": [
          {
            "title": "系统设置",
            "icon": "cogs",
            "children": [
              {
                "title": "菜单管理",
                "path": "/system/menu",
                "icon": "th-list"
              }
            ]
          },
          {
            "title": "组织架构",
            "icon": "pie-chart",
            "children": [
              {
                "title": "部门管理",
                "icon": "html5"
              },
              {
                "title": "职位管理",
                "icon": "opencart"
              }
            ]
          }
        ]
      }
    ],
    "accessInterfaces": [
      {
        "path": "/menu/:id",
        "method": "get"
      },
      {
        "path": "/menu",
        "method": "get"
      },
      {
        "path": "/menu/save",
        "method": "post"
      },
      {
        "path": "/interface/paged",
        "method": "get"
      }
    ],
    "isAdmin": 0,
    "avatarUrl": "https://api.adorable.io/avatars/85/abott@adorable.png"
    }

 */