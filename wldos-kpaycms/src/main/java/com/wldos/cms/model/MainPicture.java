/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 作品详情主图。
 *
 * @author 树悉猿
 * @date 2021/7/7
 * @version 1.0
 */
@JsonIgnoreProperties({"noMainPicPath"})
public class MainPicture {
	private String key;
	private String url;

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
