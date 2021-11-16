

package com.wldos.cms.vo;

import java.util.List;


public class SeoCrumbs {
	private String title;
	private String description;
	private String keywords;
	private List<Breadcrumb> crumbs;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public List<Breadcrumb> getCrumbs() {
		return crumbs;
	}

	public void setCrumbs(List<Breadcrumb> crumbs) {
		this.crumbs = crumbs;
	}
}
