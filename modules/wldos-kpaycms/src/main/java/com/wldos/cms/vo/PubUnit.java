/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wldos.common.res.Integer2JsonSerializer;
import com.wldos.platform.support.term.dto.Term;

/**
 * 发布内容列表单元信息。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@JsonIgnoreProperties({ "pubContent", "domainId" })
public class PubUnit extends BookUnit {
	private Long id;

	private String pubTitle;

	private String pubExcerpt;

	private String pubContent; // 辅助生成摘要

	private String cover; // 扩展属性：封面url

	private Timestamp createTime;

	private Long createBy;

	private String pubType;

	private String pubStatus;

	private Long domainId; // 用于查询分类、标签信息

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer commentCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer starCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer likeCount;

	private PubMember member;

	/** 标签列表 */
	private List<Term> tags;

	private String pubName;

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

	public String getPubExcerpt() {
		return pubExcerpt;
	}

	public void setPubExcerpt(String pubExcerpt) {
		this.pubExcerpt = pubExcerpt;
	}

	public String getPubContent() {
		return pubContent;
	}

	public void setPubContent(String pubContent) {
		this.pubContent = pubContent;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	@Override
	public Timestamp getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getStarCount() {
		return starCount;
	}

	public void setStarCount(Integer starCount) {
		this.starCount = starCount;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public String getPubStatus() {
		return pubStatus;
	}

	public void setPubStatus(String pubStatus) {
		this.pubStatus = pubStatus;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public PubMember getMember() {
		return member;
	}

	public void setMember(PubMember member) {
		this.member = member;
	}

	public List<Term> getTags() {
		return tags;
	}

	public void setTags(List<Term> tags) {
		this.tags = tags;
	}

	public String getPubName() {
		return pubName;
	}

	public void setPubName(String pubName) {
		this.pubName = pubName;
	}
}
