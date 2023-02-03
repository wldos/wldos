/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
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

	public Breadcrumb() {}

	public static Breadcrumb of(String path, String breadcrumbName) {
		return new Breadcrumb(path, breadcrumbName);
	}

	private Breadcrumb(String path, String breadcrumbName) {
		this.path = path;
		this.breadcrumbName = breadcrumbName;
	}

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
