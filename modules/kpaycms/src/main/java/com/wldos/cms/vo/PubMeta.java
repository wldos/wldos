/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.wldos.common.vo.SelectOption;

/**
 * 配置内容信息。
 *
 */
public class PubMeta {

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

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	private Integer versions;

	private List<SelectOption> termTypeIds;

	private List<String> tagIds;

	private Geographic geographic;
	// todo 后期可以改成<String, PubTypeExt>，以描述每个扩展属性的元信息，比如数据类型、取值来源，以展示动态表单或者展示动态内容
	private Map<String, String> pubTypeExt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPubContent() {
		return pubContent;
	}

	public void setPubContent(String pubContent) {
		this.pubContent = pubContent;
	}

	public String getPubTitle() {
		return pubTitle;
	}

	public void setPubTitle(String pubTitle) {
		this.pubTitle = pubTitle;
	}

	public String getPubExcerpt() {
		return pubExcerpt;
	}

	public void setPubExcerpt(String pubExcerpt) {
		this.pubExcerpt = pubExcerpt;
	}

	public String getPubStatus() {
		return pubStatus;
	}

	public void setPubStatus(String pubStatus) {
		this.pubStatus = pubStatus;
	}

	public String getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}

	public String getPubPassword() {
		return pubPassword;
	}

	public void setPubPassword(String pubPassword) {
		this.pubPassword = pubPassword;
	}

	public String getPubName() {
		return pubName;
	}

	public void setPubName(String pubName) {
		this.pubName = pubName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getComId() {
		return comId;
	}

	public void setComId(Long comId) {
		this.comId = comId;
	}

	public String getPubMimeType() {
		return pubMimeType;
	}

	public void setPubMimeType(String pubMimeType) {
		this.pubMimeType = pubMimeType;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCreateIp() {
		return createIp;
	}

	public void setCreateIp(String createIp) {
		this.createIp = createIp;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(String updateIp) {
		this.updateIp = updateIp;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Integer getVersions() {
		return versions;
	}

	public void setVersions(Integer versions) {
		this.versions = versions;
	}

	public List<SelectOption> getTermTypeIds() {
		return termTypeIds;
	}

	public void setTermTypeIds(List<SelectOption> termTypeIds) {
		this.termTypeIds = termTypeIds;
	}

	public List<String> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<String> tagIds) {
		this.tagIds = tagIds;
	}

	public Geographic getGeographic() {
		return geographic;
	}

	public void setGeographic(Geographic geographic) {
		this.geographic = geographic;
	}

	public Map<String, String> getPubTypeExt() {
		return this.pubTypeExt;
	}

	public void setPubTypeExt(Map<String, String> pubTypeExt) {
		this.pubTypeExt = pubTypeExt;
	}
}
