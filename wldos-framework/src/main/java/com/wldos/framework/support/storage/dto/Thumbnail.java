/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 缩略图定义。
 *
 * @author 元悉宇宙
 * @date 2021/7/4
 * @version 1.0
 */
public class Thumbnail {
	private String type;

	private Integer width;

	private Integer height;

	private String path;

	@JsonProperty("mimeType")
	private String mimeType;

	/* bean类要保留无参构造，否则json反序列化会失败 */
	public Thumbnail() {
	}

	/**
	 * 获取一个缩略图实例
	 *
	 * @param width 像素宽
	 * @param height 像素高
	 * @param path 文件路径
	 * @return Thumbnail实例
	 */
	public static Thumbnail of(Integer width, Integer height, String path) {
		return new Thumbnail(null, width, height, path, null);
	}

	public Thumbnail(String type, Integer width, Integer height, String path, String mimeType) {
		this.type = type;
		this.width = width;
		this.height = height;
		this.path = path;
		this.mimeType = mimeType;
	}

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