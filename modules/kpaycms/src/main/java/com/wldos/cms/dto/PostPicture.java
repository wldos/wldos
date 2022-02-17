/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.dto;

import java.util.List;

import com.wldos.support.storage.dto.Thumbnail;

/**
 * 帖子图片元数据结构。
 *
 * @author 树悉猿
 * @date 2021/7/5
 * @version 1.0
 */
public class PostPicture {
	private int width;

	private int height;

	private String path;

	// 缩略图密度集
	private List<Thumbnail> srcset;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Thumbnail> getSrcset() {
		return srcset;
	}

	public void setSrcset(List<Thumbnail> srcset) {
		this.srcset = srcset;
	}
}
