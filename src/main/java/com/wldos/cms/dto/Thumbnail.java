/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */
package com.wldos.cms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 缩略图定义。
 *
 * @Title Thumbnail
 * @Package com.wldos.cms.dto
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/7/4
 * @Version 1.0
 */
public class Thumbnail {
	private String type;

	private Integer width;

	private Integer height;

	private String path;

	@JsonProperty("mimeType")
	private String mimeType;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
