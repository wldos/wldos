/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;

import com.wldos.common.vo.SelectOption;
import com.wldos.support.cms.dto.PubTypeExt;

/**
 * 不能匹配的属性进入map以扩展字段处理
 * 凡是不能被Pub直接解析的字段统一存入metadata。
 * metadata的使用由系统默认组件和插件分别驱动，通过统一封装的方法各取所取。
 * 整个过程包括输入扩展、存储扩展、输出扩展。
 * 输入扩展：动态表单(1.hook绑定编辑块，2.统一封装meta编辑块)。
 * 存储扩展：先pub，再metadata，存储数据可hook过滤器。
 * 输出扩展：确定展现视图，适当的视图嵌入动态展现块。
 *
 */
public class Pub {

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

	private List<PubTypeExt> pubTypeExt;

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

	public List<PubTypeExt> getPubTypeExt() {
		return pubTypeExt;
	}

	public void setPubTypeExt(List<PubTypeExt> pubTypeExt) {
		this.pubTypeExt = pubTypeExt;
	}
}
