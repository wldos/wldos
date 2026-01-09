/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.entity;


import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wldos.common.res.Integer2JsonSerializer;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * pub原义为酒吧，本平台指一个内容发布，引申为publish(发布、出版、发表)、public(大众的、公共的、平民的)，指代一切可线上化或关联的分享。
 * 它可以是图文、视频、音频或者其他不限格式的存档，其体裁形式不限，展现方式不限，在系统支持的基础上不作任何限制。
 */
@Table
@Getter
@Setter
public class KPubs {

	@Id
	private Long id;

	private String pubContent;

	private String pubTitle;

	private String pubExcerpt;

	private String pubStatus;

	private String commentStatus;

	private String pubPassword;

	private String pubName;

	private Long parentId;

	private String pubType;

	private Long domainId;

	private Long comId;

	private String pubMimeType;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer commentCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer starCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer likeCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer views;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	@Version
	private Integer versions;
}
