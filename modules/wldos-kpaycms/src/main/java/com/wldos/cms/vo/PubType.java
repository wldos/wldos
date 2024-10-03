/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.cms.vo;

/**
 * 发布类型参数。
 *
 * @author 元悉宇宙
 * @date 2023/8/13
 * @version 1.0
 */
public class PubType {
	private Long id;
	private String pubType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}
}
