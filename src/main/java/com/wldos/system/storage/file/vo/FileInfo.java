/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.storage.file.vo;

/**
 * 文件信息。
 *
 * @Title FileInfo
 * @Package com.wldos.system.storage.file.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/30
 * @Version 1.0
 */
public class FileInfo {
	private Long id;
	private String name;
	private String path;
	private String url;
	private String mimeType;

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
