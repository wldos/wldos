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
 * 组织
 */
@Table
@Getter
@Setter
@RbacRowScope(comId = true, domainId = true, domainSharedRead = true)
public class WoOrg {
	@Id
	private Long id;

	private String orgCode;

	private String orgName;

	private String orgLogo;

	private String orgType;

	private Long archId;

	private Long comId;

	private Long domainId;

	private Long parentId;

	private Long displayOrder;

	private String isValid;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	/** 系统保留身份锚点（1=是，如 admin/tadmin/dadmin/can_trust 四个内置组织）：禁止改 orgCode、禁止删除 */
	private String isSystem;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;

	public WoOrg() {
	}

	public WoOrg(Long id, String orgName) {
		this.id = id;
		this.orgName = orgName;
	}

	public boolean isSystemReserved() {
		return SystemReservedEnum.isYes(this.isSystem);
	}

	public void markSystemReserved() {
		this.isSystem = SystemReservedEnum.YES.toString();
	}
}
