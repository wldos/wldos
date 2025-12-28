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
import com.wldos.platform.support.term.dto.Term;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 管理列表发布内容信息。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@ApiModel(description = "管理列表发布内容信息")
@Getter
@Setter
public class AuditPub {
	@ApiModelProperty(value = "内容ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "内容标题")
	private String pubTitle;

	@ApiModelProperty(value = "发布类型", example = "POST")
	private String pubType;

	@ApiModelProperty(value = "创建时间", example = "2023-01-01 00:00:00")
	private Timestamp createTime;

	@ApiModelProperty(value = "创建人ID", example = "1")
	private Long createBy;

	@ApiModelProperty(value = "评论数", example = "10")
	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer commentCount;

	@ApiModelProperty(value = "收藏数", example = "5")
	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer starCount;

	@ApiModelProperty(value = "点赞数", example = "20")
	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer likeCount;

	/** 查看数 */
	@ApiModelProperty(value = "查看数", example = "100")
	protected String views;

	@ApiModelProperty(value = "作者信息")
	private PubMember member;

	@ApiModelProperty(value = "分类列表")
	private List<Term> terms; // 一个发布内容可以属于多个分类

	/** 标签列表 */
	@ApiModelProperty(value = "标签列表")
	private List<Term> tags;

	@ApiModelProperty(value = "发布状态", example = "PUBLISH")
	private String pubStatus;
}
