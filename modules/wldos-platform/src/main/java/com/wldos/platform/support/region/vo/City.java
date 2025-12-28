/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.region.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 城市信息。
 *
 * @author 元悉宇宙
 * @date 2021/6/7
 * @version 1.0
 */
@ApiModel(description = "城市信息")
@Getter
@Setter
public class City {
	@ApiModelProperty(value = "城市ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "城市名称", example = "北京市")
	private String name;

	@ApiModelProperty(value = "省份ID", example = "1")
	private Long parentId;

	@ApiModelProperty(value = "省份名称", example = "北京市")
	private String provName;

	public City() {
	}

	public City(Long id, String name, Long parentId, String provName) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.provName = provName;
	}
}
