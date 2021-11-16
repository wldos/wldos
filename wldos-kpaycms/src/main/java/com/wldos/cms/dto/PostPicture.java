

package com.wldos.cms.dto;

import java.util.List;


public class PostPicture {
	private int width;

	private int height;

	private String path;


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
