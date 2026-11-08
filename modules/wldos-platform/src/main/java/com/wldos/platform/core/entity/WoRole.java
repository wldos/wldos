/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import io.github.wldos.common.enums.SystemReservedEnum;
import io.github.wldos.platform.support.rbac.RbacRowScope;

import java.sql.Timestamp;

/**
 * 角色
 */
@Table
@Getter
@Setter
@RbacRowScope(comId = true, domainId = true, domainSharedRead = true)
public class WoRole {
	@Id
	private Long id;

	private String roleCode;

	private String roleName;

	private String roleDesc;

	private String roleType;

	private Long parentId;

	private Long displayOrder;

	private String isValid;

	private Long createBy;

	private String createIp;

	private Timestamp createTime;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	/** 系统保留身份锚点（1=是，如 admin/admins 两个内置角色）：禁止改 roleCode、禁止删除 */
	private String isSystem;

	/** RBAC 数据策略：SELF/ORG/ORG_SUBTREE/TENANT/DOMAIN/TENANT_DOMAIN/ALL */
	private String dataPolicy;

	/** SHARED=允许多站点各自定制；SELF=仅定义行归属层 */
	private String shareScope;

	/** 租户公司，TENANT 数据权限过滤 */
	private Long comId;

	/** 站点域，DOMAIN 数据权限过滤 */
	private Long domainId;

	@Version
	private Integer versions;

	public WoRole() {
	}

	public WoRole(Long id, String roleName) {
		this.id = id;
		this.roleName = roleName;
	}

	public boolean isSystemReserved() {
		return SystemReservedEnum.isYes(this.isSystem);
	}

	public void markSystemReserved() {
		this.isSystem = SystemReservedEnum.YES.toString();
	}
}
