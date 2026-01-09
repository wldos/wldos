/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.resource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * 资源(菜单、管理菜单、接口服务、插件菜单、数据服务、静态资源、其他可访问资源@ResourceEnum)
 */
@Table("wo_resource")
@Getter
@Setter
public class WoResource {
	@Id
	private Long id;

	private String resourceCode;

	private String resourceName;

	private String resourcePath;

	private String componentPath;

	private String icon;

	private String resourceType;

	private String requestMethod;

	private String target;

	private Long appId;

	private Long parentId;

	private Long displayOrder;

	private String isValid;

	private String remark;

	private Long createBy;

	private String createIp;

	private Timestamp createTime;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;

	public WoResource() {
	}

	public WoResource(Long id, String resourceName) {
		this.id = id;
		this.resourceName = resourceName;
	}

	public WoResource(String resourceCode, String resourceName, String resourcePath, String icon, String resourceType, String requestMethod, String target, Long appId, Long parentId, Long displayOrder, String remark) {
		this.resourceCode = resourceCode;
		this.resourceName = resourceName;
		this.resourcePath = resourcePath;
		this.icon = icon;
		this.resourceType = resourceType;
		this.requestMethod = requestMethod;
		this.target = target;
		this.appId = appId;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
		this.remark = remark;
	}
}
