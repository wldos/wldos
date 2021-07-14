/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth.vo;

import com.wldos.system.auth.JWT;

/**
 * 登录成功返回的用户基础信息。
 *
 * @Title UserInfo
 * @Package com.wldos.system.auth.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/30
 * @Version 1.0
 */
public class UserInfo {
	private Long id;
	private String username;
	private String name;
	private String remark;
	private String avatar;
	private Long tenantId; // 主企业id

	public UserInfo() {}

	public UserInfo(JWT jwt) {
		this.id = jwt.getUserId();
		this.username = jwt.getLoginName();
		this.name = jwt.getAccountName();
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

	public String getName() {
		return name;
	}

	public void setName(String accountName) {
		this.name = accountName;
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
