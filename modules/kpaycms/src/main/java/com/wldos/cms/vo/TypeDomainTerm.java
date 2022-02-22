/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 内容领域和分类类型。
 *
 * @author 树悉猿
 * @date 2021/8/31
 * @version 1.0
 */
public class TypeDomainTerm {
	/** 内容领域类型，业务领域标识 */
	private String contentType;

	/** 分类项别名，分类项可能是分类法任一 */
	private String slugTerm;

	/** 模板类型：product、archives */
	private String tempType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSlugTerm() {
		return slugTerm;
	}

	public void setSlugTerm(String slugTerm) {
		this.slugTerm = slugTerm;
	}

	public String getTempType() {
		return tempType;
	}

	public void setTempType(String tempType) {
		this.tempType = tempType;
	}
}
