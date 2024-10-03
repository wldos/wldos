/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.cms.vo;

/**
 * 面包屑模型。
 *
 * @author 元悉宇宙
 * @date 2021/8/30
 * @version 1.0
 */
public class Breadcrumb {
	private String path;

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
