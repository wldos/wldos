/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 面包屑模型。
 *
 * @author 树悉猿
 * @date 2021/8/30
 * @version 1.0
 */
public class Breadcrumb {
	private String path;

	private String breadcrumbName;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBreadcrumbName() {
		return breadcrumbName;
	}

	public void setBreadcrumbName(String breadcrumbName) {
		this.breadcrumbName = breadcrumbName;
	}
}
