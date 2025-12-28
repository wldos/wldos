/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;

import com.wldos.framework.mvc.entity.BaseEntity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色资源授权
 */
@Table
@Getter
@Setter
public class WoAuthRole extends BaseEntity {

	private Long roleId;

	private Long resourceId;

	private Long appId;

	public WoAuthRole() {
	}

	public static WoAuthRole of(Long roleId, Long resourceId, Long appId) {
		return new WoAuthRole(roleId, resourceId, appId);
	}

	private WoAuthRole(Long roleId, Long resourceId, Long appId) {
		this.roleId = roleId;
		this.resourceId = resourceId;
		this.appId = appId;
	}
}