/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.wldos.platform.support.cms.model.CMeta;
import com.wldos.platform.support.cms.model.IMeta;
import com.wldos.platform.support.cms.vo.SeoCrumbs;
import com.wldos.platform.support.term.dto.Term;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 一篇文章或一个章节。
 *
 * @author 元悉宇宙
 * @date 2021/8/20
 * @version 1.0
 */
@ApiModel(description = "文章或章节信息")
@Getter
@Setter
public class Article extends CMeta implements IMeta {

	@ApiModelProperty(value = "内容ID", example = "1")
	// 内容pub id
	private Long id;

	@ApiModelProperty(value = "标题", example = "文章标题")
	private String pubTitle;

	@ApiModelProperty(value = "内容正文", example = "文章内容...")
	private String pubContent;

	@ApiModelProperty(value = "MIME类型", example = "text/html")
	private String pubMimeType;

	@ApiModelProperty(value = "内容别名", example = "article-alias")
	private String pubName;

	@ApiModelProperty(value = "发布类型", example = "POST")
	private String pubType;

	@ApiModelProperty(value = "父级ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "作者信息")
	private PubMember member;

	@ApiModelProperty(value = "评论数", example = "10")
	private Long commentCount;

	@ApiModelProperty(value = "收藏数", example = "5")
	private Long starCount;

	@ApiModelProperty(value = "点赞数", example = "20")
	private Long likeCount;

	@ApiModelProperty(value = "创建时间", example = "2023-01-01 00:00:00")
	private Timestamp createTime;

	@ApiModelProperty(value = "域ID", example = "1")
	private Long domainId;

	@ApiModelProperty(value = "租户ID", example = "1")
	private Long comId;

	@ApiModelProperty(value = "评论列表")
	private List<Comment> comments;

	/** 标签列表 */
	@ApiModelProperty(value = "标签列表")
	private List<Term> tags;

	@ApiModelProperty(value = "分类ID列表")
	private List<Long> termTypeIds; // 一个帖子可以属于多个分类

	/** 上一篇 */
	@ApiModelProperty(value = "上一篇文章")
	private MiniPub prev;

	/** 下一篇 */
	@ApiModelProperty(value = "下一篇文章")
	private MiniPub next;

	/** 相关文章 */
	@ApiModelProperty(value = "相关文章列表")
	private List<MiniPub> relPubs;

	/** seo和面包屑数据 */
	@ApiModelProperty(value = "SEO和面包屑数据")
	private SeoCrumbs seoCrumbs;

	public Article() {}

	private Article(Long id, String pubType) {
		this.id = id;
		this.pubType = pubType;
	}

	public static Article of(PubType pubType) {
		return new Article(pubType.getId(), pubType.getPubType());
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
}
