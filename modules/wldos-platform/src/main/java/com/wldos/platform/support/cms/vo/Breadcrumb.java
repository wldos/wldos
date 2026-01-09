/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 面包屑模型。
 *
 * @author 元悉宇宙
 * @date 2021/8/30
 * @version 1.0
 */
@ApiModel(description = "面包屑模型")
@Getter
@Setter
public class Breadcrumb {
	@ApiModelProperty(value = "路径", example = "/category/product")
	private String path;

	@ApiModelProperty(value = "面包屑名称", example = "产品分类")
	private String breadcrumbName;

	public Breadcrumb() {
	}

	public static Breadcrumb of(String path, String breadcrumbName) {
		return new Breadcrumb(path, breadcrumbName);
	}

	private Breadcrumb(String path, String breadcrumbName) {
		this.path = path;
		this.breadcrumbName = breadcrumbName;
	}
}
