/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.storage.vo;

/**
 * 文件信息。
 *
 * @author 树悉猿
 * @date 2021/5/30
 * @version 1.0
 */
public class FileInfo {
	private Long id;
	private String name;
	private String path;
	private String url;
	private String mimeType;

	public FileInfo(Long id, String name, String path, String url, String mimeType) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.url = url;
		this.mimeType = mimeType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
