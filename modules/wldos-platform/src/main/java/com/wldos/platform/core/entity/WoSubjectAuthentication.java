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
@RbacRowScope(comId = true, userId = true)
public class WoSubjectAuthentication {
	@Id
	private Long id;

	private Long subjectTypeId;

	private String subjectCode;

	private String subjectName;

	private Long userId;

	/** 归属租户（null/0=平台共享主体） */
	private Long comId;

	/** 归属站点（null/0=租户级主体） */
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
