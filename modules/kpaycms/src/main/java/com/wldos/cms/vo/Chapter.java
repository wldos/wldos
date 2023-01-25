/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 作品的章节。
 *
 * @author 树悉猿
 * @date 2021/6/22
 * @version 1.0
 */
public class Chapter {
	private Long id;

	private String pubTitle;

	private String pubContent;

	private Long parentId;

	private String pubStatus;

	private String industryType;

	public Chapter() {
	}

	public Chapter(Long id, String pubTitle, String pubContent, Long parentId, String pubStatus, String industryType) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.pubContent = pubContent;
		this.parentId = parentId;
		this.pubStatus = pubStatus;
		this.industryType = industryType;
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

	public String getIndustryType() {
		return industryType;
	}

	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}
}
