/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 路由参数，生成路由的依据，一般用模板类型和分类法构成。
 *
 * @author 树悉猿
 * @date 2021/8/31
 * @version 1.0
 */
public class RouteParams {
	/** 模板类型：product、archives、info等 */
	private String tempType;

	/** 分类项别名，分类项可能是分类法任一 */
	private String slugTerm;

	public RouteParams() {}

	public static RouteParams of(String tempType, String slugTerm) {
		return new RouteParams(tempType, slugTerm);
	}
	private RouteParams(String tempType, String slugTerm) {
		this.tempType = tempType;
		this.slugTerm = slugTerm;
	}

	public String getTempType() {
		return tempType;
	}

	public void setTempType(String tempType) {
		this.tempType = tempType;
	}

	public String getSlugTerm() {
		return slugTerm;
	}

	public void setSlugTerm(String slugTerm) {
		this.slugTerm = slugTerm;
	}
}
