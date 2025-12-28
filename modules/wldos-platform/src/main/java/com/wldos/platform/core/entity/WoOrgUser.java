/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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

@Table
@Getter
@Setter
public class WoOrgUser {
	@Id
	private Long id;

	private Long userId;

	private Long userComId;

	private Long orgId;

	private Long archId;

	private Long comId;

	private String isValid;

	private Long createBy;

	private String createIp;

	private java.sql.Timestamp createTime;

	private Long updateBy;

	private String updateIp;

	private java.sql.Timestamp updateTime;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;
}
