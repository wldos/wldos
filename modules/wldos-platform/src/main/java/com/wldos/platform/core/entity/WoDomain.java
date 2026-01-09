/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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

@Table
@Getter
@Setter
public class WoDomain {
	@Id
	private Long id;

	private Long comId;

	private String siteDomain;

	private String secondDomain;

	private String siteName;

	private String siteUrl;

	private String siteLogo;

	private String favicon;

	private String siteTitle;

	private String siteKeyword;

	private String siteDescription;

	private String slogan;

	private String foot;

	private String flink;

	private String copy;

	private Long parentId;

	private Long displayOrder;

	private String cnameDomain;

	private String isValid;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	@Version
	private Integer versions;
}