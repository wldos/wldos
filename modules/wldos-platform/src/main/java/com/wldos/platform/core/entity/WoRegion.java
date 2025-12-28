/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;


import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 注意：约定code字面值必须等于id，id必须与国标一致
 */
@Table
@Getter
@Setter
public class WoRegion {

	@Id
	private Long id;

	private String regionCode;

	private String name;

	private String level;

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
}
