/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.vo;

/**
 * 动态组件路由。
 *
 * @author 树悉猿
 * @date 2021/9/8
 * @version 1.0
 */
public class Route {
	/** 对应组件的包装对象名称，js实现类的类名 */
	private String module;

	/** 绑定的分类别名 */
	private String category;

	/** 绑定的行业门类 */
	private String industry;

	public Route() {
	}

	public Route(String module, String category, String industry) {
		this.module = module;
		this.category = category;
		this.industry = industry;
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

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
}
