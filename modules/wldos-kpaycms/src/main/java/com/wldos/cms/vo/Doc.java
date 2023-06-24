/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.vo;

import java.util.List;

/**
 * 文档，一个文档可以有章节。
 *
 * @author 树悉猿
 * @date 2023/5/14
 * @version 1.0
 */
public class Doc {
	private Long id;

	private String pubTitle;

	private List<DocItem> chapter;

	public Doc() {
	}

	public static Doc of(Long id, String pubTitle, List<DocItem> chapters) {
		return new Doc(id, pubTitle, chapters);
	}

	private Doc(Long id, String pubTitle, List<DocItem> chapter) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.chapter = chapter;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPubTitle() {
		return pubTitle;
	}

	public void setPubTitle(String pubTitle) {
		this.pubTitle = pubTitle;
	}

	public List<DocItem> getChapter() {
		return chapter;
	}

	public void setChapter(List<DocItem> chapter) {
		this.chapter = chapter;
	}
}
