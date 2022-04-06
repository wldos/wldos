/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wldos.common.res.Integer2JsonSerializer;
import com.wldos.sys.base.dto.Term;

/**
 * 供求信息列表单元。
 *
 * @author 树悉猿
 * @date 2022/01/05
 * @version 1.0
 */
@JsonIgnoreProperties({ "postContent", "domainId" })
public class InfoUnit {
	private Long id;

	private String postTitle;

	private String postExcerpt;

	private String postContent; // 辅助生成摘要

	private BigDecimal ornPrice = BigDecimal.valueOf(0);

	private String cover; // 扩展属性：封面url

	private String prov;

	private String city;

	private Timestamp updateTime;

	private Long createBy;

	private String postType;

	private String contentType;

	private Long domainId; // 用于查询分类、标签信息

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer commentCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer starCount;

	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer likeCount;

	private PostMember member;

	/** 标签列表 */
	private List<Term> tags;

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

	public BigDecimal getOrnPrice() {
		return ornPrice;
	}

	public void setOrnPrice(BigDecimal ornPrice) {
		this.ornPrice = ornPrice;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
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

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public PostMember getMember() {
		return member;
	}

	public void setMember(PostMember member) {
		this.member = member;
	}

	public List<Term> getTags() {
		return tags;
	}

	public void setTags(List<Term> tags) {
		this.tags = tags;
	}
}
