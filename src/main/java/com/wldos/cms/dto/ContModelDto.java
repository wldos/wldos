/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.dto;

import java.sql.Timestamp;

/**
 * 内容模型。
 *
 * @author 树悉猿
 * @date 2021/6/19
 * @version 1.0
 */
public class ContModelDto {
	private Long id;

	private String postTitle;

	private String postExcerpt;

	private String postContent;

	private String postStatus;

	private String commentStatus;

	private String postPassword;

	private String postName;

	private String pingStatus;

	private String toPing;

	private String pinged;

	private Long parentId;

	private String guid;

	private String postType;

	private String postMimeType;

	private Long commentCount;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	private Long modelId;

	private String modelCode;

	private Long contentId;

	private String contentCode;

	private Long termTypeId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getPostExcerpt() {
		return postExcerpt;
	}

	public void setPostExcerpt(String postExcerpt) {
		this.postExcerpt = postExcerpt;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public String getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(String postStatus) {
		this.postStatus = postStatus;
	}

	public String getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}

	public String getPostPassword() {
		return postPassword;
	}

	public void setPostPassword(String postPassword) {
		this.postPassword = postPassword;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getPingStatus() {
		return pingStatus;
	}

	public void setPingStatus(String pingStatus) {
		this.pingStatus = pingStatus;
	}

	public String getToPing() {
		return toPing;
	}

	public void setToPing(String toPing) {
		this.toPing = toPing;
	}

	public String getPinged() {
		return pinged;
	}

	public void setPinged(String pinged) {
		this.pinged = pinged;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getPostMimeType() {
		return postMimeType;
	}

	public void setPostMimeType(String postMimeType) {
		this.postMimeType = postMimeType;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
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

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getContentCode() {
		return contentCode;
	}

	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}

	public Long getTermTypeId() {
		return termTypeId;
	}

	public void setTermTypeId(Long termTypeId) {
		this.termTypeId = termTypeId;
	}
}
