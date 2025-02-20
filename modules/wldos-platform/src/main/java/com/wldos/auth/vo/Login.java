/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.auth.vo;

import java.util.List;

import com.wldos.support.auth.vo.Token;

/**
 * 登录后返回用户认证和权限信息。
 *
 * @author 元悉宇宙
 * @date 2021-04-30
 * @version V1.0
 */
public class Login {
	/** 登录状态 */
	private String status;

	/** 错误信息 */
	private String news;

	/** 访问token和刷新token */
	private Token token;

	/** 用户的权限：默认排除菜单权限 */
	private List<String> currentAuthority;

	/** 登录类型：account、mobile */
	private String type;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public List<String> getCurrentAuthority() {
		return currentAuthority;
	}

	public void setCurrentAuthority(List<String> currentAuthority) {
		this.currentAuthority = currentAuthority;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}