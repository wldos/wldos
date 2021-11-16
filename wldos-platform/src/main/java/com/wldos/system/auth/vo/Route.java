/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.system.auth.vo;

/**
 * 动态组件路由。
 *
 * @author 树悉猿
 * @date 2021/9/8
 * @version 1.0
 */
public class Route {

	private String module;

	private String category;

	private String content;

	public Route() {}

	public Route(String module, String category, String content) {
		this.module = module;
		this.category = category;
		this.content = content;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
