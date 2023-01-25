/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;

/**
 * 作品列表单元信息。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public class BookUnit {
	private Long id;

	private String pubTitle;

	private String industryType;

	private String subTitle; // 扩展属性：副标题

	private String cover; // 扩展属性：封面url

	private Timestamp updateTime;

	private Long createBy;

	private List<PubMember> members;

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

	public String getIndustryType() {
		return industryType;
	}

	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public List<PubMember> getMembers() {
		return members;
	}

	public void setMembers(List<PubMember> members) {
		this.members = members;
	}
}
