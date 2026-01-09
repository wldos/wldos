/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 发布内容列表单元信息。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@ApiModel(description = "发布内容列表单元信息")
@JsonIgnoreProperties({ "pubContent", "domainId" })
@Getter
@Setter
public class PubUnit extends BookUnit {
	@ApiModelProperty(value = "内容ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "标题", example = "内容标题")
	private String pubTitle;

	@ApiModelProperty(value = "摘要", example = "内容摘要")
	private String pubExcerpt;

	@ApiModelProperty(value = "内容正文（辅助生成摘要）", hidden = true)
	private String pubContent; // 辅助生成摘要

	@ApiModelProperty(value = "封面URL", example = "https://example.com/cover.jpg")
	private String cover; // 扩展属性：封面url

	@ApiModelProperty(value = "创建时间", example = "2023-01-01 00:00:00")
	private Timestamp createTime;

	@ApiModelProperty(value = "创建人ID", example = "1")
	private Long createBy;

	@ApiModelProperty(value = "发布类型", example = "POST")
	private String pubType;

	@ApiModelProperty(value = "发布状态", example = "PUBLISH")
	private String pubStatus;

	@ApiModelProperty(value = "域ID（用于查询分类、标签信息）", hidden = true)
	private Long domainId; // 用于查询分类、标签信息

	@ApiModelProperty(value = "评论数", example = "10")
	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer commentCount;

	@ApiModelProperty(value = "收藏数", example = "5")
	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer starCount;

	@ApiModelProperty(value = "点赞数", example = "20")
	@JsonSerialize(using = Integer2JsonSerializer.class)
	private Integer likeCount;

	@ApiModelProperty(value = "作者信息")
	private PubMember member;

	/** 标签列表 */
	@ApiModelProperty(value = "标签列表")
	private List<Term> tags;

	@ApiModelProperty(value = "内容别名", example = "content-alias")
	private String pubName;
}
