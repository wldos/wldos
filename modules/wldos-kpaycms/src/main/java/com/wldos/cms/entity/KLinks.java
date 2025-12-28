/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table
@Getter
@Setter
public class KLinks {
	@Id
	private Long id;

	private String linkUrl;

	private String linkName;

	private String linkImage;

	private String linkTarget;

	private String linkDescription;

	private String linkVisible;

	private Long linkOwner;

	private Long linkRating;

	private Timestamp linkUpdated;

	private String linkRel;

	private String linkNotes;

	private String linkRss;
}
