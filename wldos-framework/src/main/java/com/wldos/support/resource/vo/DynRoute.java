/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.resource.vo;

/**
 * 动态路由绑定。
 *
 * @author 树悉猿
 * @date 2023/4/8
 * @version 1.0
 */
public class DynRoute {
	/** 路由path */
	private String path;

	private String module;

	/** 绑定的分类别名 */
	private DynSet conf;

	public DynRoute() {
	}

	public DynRoute(String module, DynSet dynSet) {
		this.module = module;
		this.conf = dynSet;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public DynSet getConf() {
		return conf;
	}

	public void setConf(DynSet conf) {
		this.conf = conf;
	}
}
