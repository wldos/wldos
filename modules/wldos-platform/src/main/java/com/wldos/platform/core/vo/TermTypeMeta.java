/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 分类类型元数据 VO
 *
 * @author 元悉宇宙
 * @date 2025/12/07
 * @version 1.0
 */
@ApiModel(description = "分类类型元数据")
@Getter
@Setter
public class TermTypeMeta {
	@ApiModelProperty(value = "分类类型编码", example = "category")
	private String code;
	
	@ApiModelProperty(value = "分类类型名称", example = "分类")
	private String name;
	
	@ApiModelProperty(value = "描述", example = "内容分类")
	private String description;
	
	@ApiModelProperty(value = "结构类型：tree=树形, flat=扁平", example = "tree")
	private String structureType;
	
	@ApiModelProperty(value = "是否支持排序：1=支持, 0=不支持", example = "1")
	private String supportSort;
	
	@ApiModelProperty(value = "是否支持层级：1=支持, 0=不支持", example = "1")
	private String supportHierarchy;
	
	@ApiModelProperty(value = "图标", example = "folder")
	private String icon;
	
	@ApiModelProperty(value = "排序顺序", example = "1")
	private Integer sortOrder;
	
	@ApiModelProperty(value = "是否系统类型（不可删除）", example = "true")
	private Boolean isSystem;
	
	@ApiModelProperty(value = "是否在数据库中存在", example = "true")
	private Boolean existsInDb;
}

