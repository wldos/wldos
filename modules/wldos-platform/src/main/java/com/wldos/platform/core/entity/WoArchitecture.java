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

@Table
@Getter
@Setter
public class WoArchitecture {
	@Id
	private Long id;

	private String archCode;

	private String archName;

	private String archDesc;

	private Long comId;

	private Long parentId;

	private Long displayOrder;

	private String isValid;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;

	public WoArchitecture() {
	}

	public WoArchitecture(Long id, String archName) {
		this.id = id;
		this.archName = archName;
	}
}