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
 * 动态组件路由。
 *
 * @author 元悉宇宙
 * @date 2021/9/8
 * @version 1.0
 */
@ApiModel(description = "动态组件路由")
@Getter
@Setter
public class Route {
	@ApiModelProperty(value = "对应组件的包装对象名称，js实现类的类名", example = "ProductList")
	private String module;

	@ApiModelProperty(value = "绑定的分类别名", example = "tech")
	private String category;

	public Route() {
	}

	public Route(String module, String category) {
		this.module = module;
		this.category = category;
	}
}
