/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * 内容模型。
 *
 * @author 元悉宇宙
 * @date 2021/6/19
 * @version 1.0
 */
@Getter
@Setter
public class ContModelDto {
	private Long id;

	private String PubTitle;

	private String PubExcerpt;

	private String PubContent;

	private String PubStatus;

	private String commentStatus;

	private String pubPassword;

	private String pubName;

	private Long parentId;

	private String pubType;

	private String pubMimeType;

	private Long commentCount;

	private Long starCount;

	private Long likeCount;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	private Long domainId;

	private Long comId;
}
