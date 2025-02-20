/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.entity;

import com.wldos.framework.entity.BaseEntity;

import org.springframework.data.relational.core.mapping.Table;

@Table
public class WoAuthRole extends BaseEntity {

	private Long roleId;

	private Long resourceId;

	private Long appId;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long ownerId) {
		this.roleId = ownerId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}
}