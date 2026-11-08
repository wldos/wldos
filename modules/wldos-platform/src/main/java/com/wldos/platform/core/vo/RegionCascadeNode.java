/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 行政区划级联节点（角色范围维 UI）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "行政区划级联节点")
@Getter
@Setter
public class RegionCascadeNode {

	@ApiModelProperty(value = "节点主键，与 region_code 数值一致")
	private Long id;

	@ApiModelProperty(value = "国标码")
	private String value;

	@ApiModelProperty(value = "名称")
	private String label;

	@ApiModelProperty(value = "层级：1省 2市 3区县")
	private String level;

	@ApiModelProperty(value = "是否叶子（当前授权粒度下不可再展开）")
	@JsonProperty("leaf")
	private boolean leaf;

	public RegionCascadeNode() {
	}

	public RegionCascadeNode(Long id, String value, String label, String level, boolean leaf) {
		this.id = id;
		this.value = value;
		this.label = label;
		this.level = level;
		this.leaf = leaf;
	}
}
