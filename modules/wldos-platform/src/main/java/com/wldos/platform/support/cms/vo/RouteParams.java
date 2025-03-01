/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms.vo;

/**
 * 路由参数，生成路由的依据，一般用模板类型和分类法构成。
 *
 * @author 元悉宇宙
 * @date 2021/8/31
 * @version 1.0
 */
public class RouteParams {
	/** 模板类型：product、archives、info等 */
	private String tempType;

	/** 分类项别名，分类项可能是分类法任一 */
	private String slugTerm;

	public RouteParams() {
	}

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
