/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.vo;

import java.sql.Timestamp;
import java.util.List;

import com.wldos.platform.support.cms.model.IMeta;
import com.wldos.platform.support.cms.model.KModelMeta;
import com.wldos.platform.support.term.dto.Term;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 供求信息。
 *
 * @author 元悉宇宙
 * @date 2022/01/05
 * @version 1.0
 */
@ApiModel(description = "供求信息")
@Getter
@Setter
public class Info extends KModelMeta implements IMeta {

	@ApiModelProperty(value = "内容ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "发布标题", example = "示例标题")
	private String pubTitle;

	@ApiModelProperty(value = "发布内容", example = "内容详情")
	private String pubContent;

	@ApiModelProperty(value = "发布名称", example = "示例名称")
	private String pubName;

	@ApiModelProperty(value = "父内容ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "发布类型", example = "info")
	private String pubType;

	@ApiModelProperty(value = "MIME类型", example = "text/html")
	private String pubMimeType;

	@ApiModelProperty(value = "评论数", example = "10")
	private Long commentCount;

	@ApiModelProperty(value = "创建人ID", hidden = true)
	private Long createBy;

	@ApiModelProperty(value = "创建时间", hidden = true)
	private Timestamp createTime;

	@ApiModelProperty(value = "更新人ID", hidden = true)
	private Long updateBy;

	@ApiModelProperty(value = "更新时间", hidden = true)
	private Timestamp updateTime;

	@ApiModelProperty(value = "域名ID", example = "1")
	private Long domainId;

	@ApiModelProperty(value = "公司ID", example = "1")
	private Long comId;

	@ApiModelProperty(value = "分类ID列表，一个帖子可以属于多个分类")
	private List<Long> termTypeIds;

	@ApiModelProperty(value = "标签列表")
	private List<Term> tags;

	@ApiModelProperty(value = "面包屑及SEO元素")
	private SeoCrumbs seoCrumbs;

	@ApiModelProperty(value = "真实编号", example = "INFO001")
	private String realNo;

	@Override
	public String getPubTitle() {
		return pubTitle;
	}

	@Override
	public String getPubContent() {
		return pubContent;
	}

	@Override
	public void setPubContent(String pubContent) {
		this.pubContent = pubContent;
	}
}
