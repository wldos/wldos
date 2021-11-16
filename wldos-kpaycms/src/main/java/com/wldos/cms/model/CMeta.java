

package com.wldos.cms.model;

import java.math.BigDecimal;
import java.util.List;

import com.wldos.cms.vo.ContentExt;


public class CMeta {


	protected String cover;


	protected String views;

	protected List<ContentExt> contentExt;

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public List<ContentExt> getContentExt() {
		return contentExt;
	}

	public void setContentExt(List<ContentExt> contentExt) {
		this.contentExt = contentExt;
	}
}
