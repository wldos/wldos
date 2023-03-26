/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
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
 * 一篇文章或一个章节。
 *
 * @author 树悉猿
 * @date 2021/8/20
 * @version 1.0
 */
public class Article extends CMeta implements IMeta {

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

	private Timestamp updateTime;

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

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
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
