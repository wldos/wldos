/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 作品详情主图。
 *
 * @author 元悉宇宙
 * @date 2021/7/7
 * @version 1.0
 */
@JsonIgnoreProperties({ "noMainPicPath" })
public class MainPicture {
	private String key;

	private String url;

	/** 无主图占位图路径 */
	public static final String noMainPicPath = "/noPic.jpg";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
