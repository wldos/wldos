/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
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

	private String postTitle;

	private String postContent;

	private Long parentId;

	private String postStatus;

	private String contentType;

	public Chapter() {
	}

	public Chapter(Long id, String postTitle, String postContent, Long parentId, String postStatus, String contentType) {
		this.id = id;
		this.postTitle = postTitle;
		this.postContent = postContent;
		this.parentId = parentId;
		this.postStatus = postStatus;
		this.contentType = contentType;
	}

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

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(String postStatus) {
		this.postStatus = postStatus;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
