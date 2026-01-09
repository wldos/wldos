/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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

import java.sql.Timestamp;

/**
 * 组织
 */
@Table
@Getter
@Setter
public class WoOrg {
	@Id
	private Long id;

	private String orgCode;

	private String orgName;

	private String orgLogo;

	private String orgType;

	private Long archId;

	private Long comId;

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
}
