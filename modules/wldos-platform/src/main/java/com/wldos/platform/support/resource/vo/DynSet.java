/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
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
 * 动态路由设置。
 *
 * @author 元悉宇宙
 * @date 2023/4/8
 * @version 1.0
 */
@ApiModel(description = "动态路由设置")
@Getter
@Setter
public class DynSet {
	@ApiModelProperty(value = "对应组件的包装对象名称，js实现类的类名，或者模板类型（static、url、component、other）", example = "ProductList")
	private String module;

	@ApiModelProperty(value = "绑定的分类别名", example = "tech")
	private String category;

	@ApiModelProperty(value = "指定URL或组件路径", example = "/product/list")
	private String url;

	@ApiModelProperty(value = "扩展属性（JSON格式，用于传递额外参数）", example = "{\"key\":\"value\"}")
	private String extraProps;

	public DynSet() {
	}

	public static DynSet of(String module, String category, String url) {
		return new DynSet(module, category, url);
	}

	private DynSet(String module, String category) {
		this.module = module;
		this.category = category;
	}

	private DynSet(String module, String category, String url) {
		this.module = module;
		this.category = category;
		this.url = url;
	}
}
