/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.resource.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 动态路由绑定。
 *
 * @author 元悉宇宙
 * @date 2023/4/8
 * @version 1.0
 */
@ApiModel(description = "动态路由绑定")
@Getter
@Setter
public class DynRoute {
	@ApiModelProperty(value = "路由路径", example = "/product/tech")
	private String path;

	@ApiModelProperty(value = "模块名称", example = "ProductList")
	private String module;

	@ApiModelProperty(value = "动态路由配置")
	private DynSet conf;

	public DynRoute() {
	}

	public DynRoute(String module, DynSet dynSet) {
		this.module = module;
		this.conf = dynSet;
	}
}
