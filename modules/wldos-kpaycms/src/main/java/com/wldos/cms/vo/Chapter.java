/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import com.wldos.cms.entity.KPubs;
import com.wldos.common.utils.ObjectUtils;

/**
 * 作品的章节元素，可以派生出剧集。
 *
 * @author 元悉宇宙
 * @date 2021/6/22
 * @version 1.0
 */
public class Chapter {
	private Long id;

	private String pubTitle;

	private String pubContent;

	private String pubMimeType;

	private Long parentId;

	private String pubStatus;

	public Chapter() {
	}

	public static Chapter of(KPubs single) {
		return of(single.getId(), ObjectUtils.string(single.getPubTitle()), ObjectUtils.string(single.getPubContent())/* 过滤null值，防止前端不刷新*/,
				single.getPubMimeType(), single.getParentId(), single.getPubStatus());
	}

	public static Chapter of(Long id, String pubTitle, String pubContent, String pubMimeType, Long parentId, String pubStatus) {
		return new Chapter(id, pubTitle, pubContent, pubMimeType, parentId, pubStatus);
	}

	private Chapter(Long id, String pubTitle, String pubContent, String pubMimeType, Long parentId, String pubStatus) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.pubContent = pubContent;
		this.pubMimeType = pubMimeType;
		this.parentId = parentId;
		this.pubStatus = pubStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPubTitle() {
		return pubTitle;
	}

	public void setPubTitle(String pubTitle) {
		this.pubTitle = pubTitle;
	}

	public String getPubContent() {
		return pubContent;
	}

	public void setPubContent(String pubContent) {
		this.pubContent = pubContent;
	}

	public String getPubMimeType() {
		return pubMimeType;
	}

	public void setPubMimeType(String pubMimeType) {
		this.pubMimeType = pubMimeType;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPubStatus() {
		return pubStatus;
	}

	public void setPubStatus(String pubStatus) {
		this.pubStatus = pubStatus;
	}
}
