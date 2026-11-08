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

import io.github.wldos.platform.support.rbac.RbacRowScope;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Table
@Getter
@Setter
@RbacRowScope(comId = true, domainId = true)
public class WoSubjectAssociation {
	@Id
	private Long id;

	private Long subjectTypeId;

	private Long roleId;

	/** 归属租户（null/0=平台共享授权） */
	private Long comId;

	/** 归属站点（null/0=租户级授权） */
	private Long domainId;

	private String isValid;

	private Long createBy;

	private String createIp;

	private Timestamp createTime;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	@Version
	private Integer versions;
}
