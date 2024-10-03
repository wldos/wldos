/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.resource.vo;

/**
 * 动态路由设置。
 *
 * @author 元悉宇宙
 * @date 2023/4/8
 * @version 1.0
 */
public class DynSet {
	/** 对应组件的包装对象名称，js实现类的类名 */
	private String module;

	/** 绑定的分类别名 */
	private String category;

	public DynSet() {
	}

	public DynSet(String module, String category) {
		this.module = module;
		this.category = category;
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
}
