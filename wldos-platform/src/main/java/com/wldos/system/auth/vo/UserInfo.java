/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth.vo;

import com.wldos.system.auth.JWT;

/**
 * 登录成功返回的用户基础信息。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
public class UserInfo {
	private Long id;
	private String username;
	private String nickname;
	private String remark;
	private String avatar;
	private Long tenantId;

	public UserInfo() {}

	public UserInfo(JWT jwt) {
		this.id = jwt.getUserId();
		this.tenantId = jwt.getTenantId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String headImg) {
		this.avatar = headImg;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
}
