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
 * 一个页面的数据，前端提供某种定制页面模板，然后用此数据去填充。
 *
 * @author 元悉宇宙
 * @date 2021/8/20
 * @version 1.0
 */
@ApiModel(description = "页面数据")
@Getter
@Setter
public class Page extends CMeta implements IMeta {

	@ApiModelProperty(value = "内容ID", example = "1")
	// 内容pub id
	private Long id;

	@ApiModelProperty(value = "标题", example = "页面标题")
	private String pubTitle;

	@ApiModelProperty(value = "内容正文", example = "页面内容...")
	private String pubContent;

	@ApiModelProperty(value = "页面别名", example = "page-alias")
	private String pubName;

	@ApiModelProperty(value = "发布类型", example = "PAGE")
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
	@ApiModelProperty(value = "上一页")
	private MiniPub prev;

	/** 下一篇 */
	@ApiModelProperty(value = "下一页")
	private MiniPub next;

	/** 相关文章 */
	@ApiModelProperty(value = "相关页面列表")
	private List<MiniPub> relPubs;

	/** seo和面包屑数据 */
	@ApiModelProperty(value = "SEO和面包屑数据")
	private SeoCrumbs seoCrumbs;

	@ApiModelProperty(value = "副标题", example = "页面副标题")
	private String subTitle;

	@ApiModelProperty(value = "价格", example = "100.00")
	private BigDecimal ornPrice;

	@ApiModelProperty(value = "联系人", example = "联系人姓名")
	private String contact;

	@ApiModelProperty(value = "电话", example = "13800138000")
	private String telephone;

	@ApiModelProperty(value = "真实号码", example = "13800138000")
	private String realNo;

	@ApiModelProperty(value = "城市", example = "北京市")
	private String city;

	@ApiModelProperty(value = "省份", example = "北京市")
	private String prov;

	@ApiModelProperty(value = "县区", example = "朝阳区")
	private String county;

	@Override
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	@Override
	public void setOrnPrice(BigDecimal bigDecimal) {
		this.ornPrice = bigDecimal;
	}

	@Override
	public void setContact(String hideName) {
		this.contact = hideName;
	}

	@Override
	public void setTelephone(String hidePhone) {
		this.telephone = hidePhone;
	}

	@Override
	public void setRealNo(String telephone) {
		this.realNo = telephone;
	}

	@Override
	public void setCity(String name) {
		this.city = name;
	}

	@Override
	public void setProv(String provName) {
		this.prov = provName;
	}

	@Override
	public void setCounty(String county) {
		this.county = county;
	}
}
