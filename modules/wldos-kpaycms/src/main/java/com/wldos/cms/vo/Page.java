/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.wldos.support.cms.model.CMeta;
import com.wldos.support.cms.model.IMeta;
import com.wldos.support.cms.vo.SeoCrumbs;
import com.wldos.support.term.dto.Term;

/**
 * 一个页面的数据，前端提供某种定制页面模板，然后用此数据去填充。
 *
 * @author 元悉宇宙
 * @date 2021/8/20
 * @version 1.0
 */
public class Page extends CMeta implements IMeta {

	// 内容pub id
	private Long id;

	private String pubTitle;

	private String pubContent;

	private String pubName;

	private String pubType;

	private Long parentId;

	private PubMember member;

	private Long commentCount;

	private Long starCount;

	private Long likeCount;

	private Timestamp createTime;

	private Long domainId;

	private Long comId;

	private List<Comment> comments;

	/** 标签列表 */
	private List<Term> tags;

	private List<Long> termTypeIds; // 一个帖子可以属于多个分类

	/** 上一篇 */
	private MiniPub prev;

	/** 下一篇 */
	private MiniPub next;

	/** 相关文章 */
	private List<MiniPub> relPubs;

	/** seo和面包屑数据 */
	private SeoCrumbs seoCrumbs;

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

	public String getPubName() {
		return pubName;
	}

	public void setPubName(String pubName) {
		this.pubName = pubName;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public PubMember getMember() {
		return member;
	}

	public void setMember(PubMember member) {
		this.member = member;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public Long getStarCount() {
		return starCount;
	}

	public void setStarCount(Long starCount) {
		this.starCount = starCount;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
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

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Term> getTags() {
		return tags;
	}

	public void setTags(List<Term> tags) {
		this.tags = tags;
	}

	@Override
	public void setSubTitle(String subTitle) {

	}

	@Override
	public void setOrnPrice(BigDecimal bigDecimal) {

	}

	@Override
	public void setContact(String hideName) {

	}

	@Override
	public void setTelephone(String hidePhone) {

	}

	@Override
	public void setRealNo(String telephone) {

	}

	@Override
	public void setCity(String name) {

	}

	@Override
	public void setProv(String provName) {

	}

	@Override
	public void setCounty(String county) {

	}

	public List<Long> getTermTypeIds() {
		return termTypeIds;
	}

	public void setTermTypeIds(List<Long> termTypeIds) {
		this.termTypeIds = termTypeIds;
	}

	public MiniPub getPrev() {
		return prev;
	}

	public void setPrev(MiniPub prev) {
		this.prev = prev;
	}

	public MiniPub getNext() {
		return next;
	}

	public void setNext(MiniPub next) {
		this.next = next;
	}

	public List<MiniPub> getRelPubs() {
		return relPubs;
	}

	public void setRelPubs(List<MiniPub> relPubs) {
		this.relPubs = relPubs;
	}

	public SeoCrumbs getSeoCrumbs() {
		return seoCrumbs;
	}

	public void setSeoCrumbs(SeoCrumbs seoCrumbs) {
		this.seoCrumbs = seoCrumbs;
	}
}
