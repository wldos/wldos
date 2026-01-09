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
import java.util.Map;

import com.wldos.common.vo.SelectOption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置内容信息。
 *
 */
@ApiModel(description = "配置内容信息")
@Getter
@Setter
public class PubMeta {

	@ApiModelProperty(value = "内容ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "内容正文", example = "内容正文内容...")
	private String pubContent;

	@ApiModelProperty(value = "标题", example = "标题内容")
	private String pubTitle;

	@ApiModelProperty(value = "摘要", example = "摘要内容")
	private String pubExcerpt;

	@ApiModelProperty(value = "发布状态", example = "PUBLISH")
	private String pubStatus;

	@ApiModelProperty(value = "评论状态", example = "OPEN")
	private String commentStatus;

	@ApiModelProperty(value = "访问密码", hidden = true)
	private String pubPassword;

	@ApiModelProperty(value = "内容别名", example = "content-alias")
	private String pubName;

	@ApiModelProperty(value = "父级ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "发布类型", example = "POST")
	private String pubType;

	@ApiModelProperty(value = "域ID", example = "1")
	private Long domainId;

	@ApiModelProperty(value = "租户ID", example = "1")
	private Long comId;

	@ApiModelProperty(value = "MIME类型", example = "text/html")
	private String pubMimeType;

	@ApiModelProperty(value = "创建人ID", example = "1")
	private Long createBy;

	@ApiModelProperty(value = "创建时间", example = "2023-01-01 00:00:00")
	private Timestamp createTime;

	@ApiModelProperty(value = "创建IP", example = "127.0.0.1")
	private String createIp;

	@ApiModelProperty(value = "更新人ID", example = "1")
	private Long updateBy;

	@ApiModelProperty(value = "更新时间", example = "2023-01-01 00:00:00")
	private Timestamp updateTime;

	@ApiModelProperty(value = "更新IP", example = "127.0.0.1")
	private String updateIp;

	@ApiModelProperty(value = "删除标志", example = "NORMAL")
	private String deleteFlag;

	@ApiModelProperty(value = "版本号", example = "1")
	private Integer versions;

	@ApiModelProperty(value = "分类ID列表")
	private List<SelectOption> termTypeIds;

	@ApiModelProperty(value = "标签ID列表")
	private List<String> tagIds;

	@ApiModelProperty(value = "地理信息")
	private Geographic geographic;

	@ApiModelProperty(value = "扩展属性Map")
	// todo 后期可以改成<String, PubTypeExt>，以描述每个扩展属性的元信息，比如数据类型、取值来源，以展示动态表单或者展示动态内容
	private Map<String, String> pubTypeExt;
}
