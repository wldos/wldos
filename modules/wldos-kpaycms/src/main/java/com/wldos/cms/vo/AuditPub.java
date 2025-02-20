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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wldos.common.res.Integer2JsonSerializer;
import com.wldos.support.term.dto.Term;

/**
 * 管理列表发布内容信息。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
public class AuditPub {
	private Long id;

	private String pubTitle;

	private String pubType;

	private Timestamp createTime;

	private Long createBy;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer commentCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer starCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer likeCount;

	/** 查看数 */
	protected String views;

	private PubMember member;

	private List<Term> terms; // 一个发布内容可以属于多个分类

	/** 标签列表 */
	private List<Term> tags;

	private String pubStatus;

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

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

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

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public PubMember getMember() {
		return member;
	}

	public void setMember(PubMember member) {
		this.member = member;
	}

	public List<Term> getTerms() {
		return terms;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	public List<Term> getTags() {
		return tags;
	}

	public void setTags(List<Term> tags) {
		this.tags = tags;
	}

	public String getPubStatus() {
		return pubStatus;
	}

	public void setPubStatus(String pubStatus) {
		this.pubStatus = pubStatus;
	}
}
