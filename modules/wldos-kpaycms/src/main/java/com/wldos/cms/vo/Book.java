/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.util.List;

import com.wldos.platform.core.enums.PubTypeEnum;

/**
 * 作品，以电子书为蓝本代表所有类型的作品，必要的时候再细化。
 *
 * @author 元悉宇宙
 * @date 2021/6/22
 * @version 1.0
 */
public class Book {
	private Long id;

	private String pubTitle;

	private String pubType;

	/** 是否单体类型：'1'是，'0'否*/
	private Boolean isSingle;

	private String pubStatus;

	private List<Chapter> chapter;

	public Book() {
	}

	public static Book of(Long id, String pubTitle, String pubType, String pubStatus, List<Chapter> chapters) {
		return new Book(id, pubTitle, pubType, PubTypeEnum.isSingle(pubType), pubStatus, chapters);
	}

	private Book(Long id, String pubTitle, String pubType, Boolean isSingle, String pubStatus, List<Chapter> chapter) {
		this.id = id;
		this.pubTitle = pubTitle;
		this.pubType = pubType;
		this.isSingle = isSingle;
		this.pubStatus = pubStatus;
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

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public Boolean getIsSingle() {
		return isSingle;
	}

	public void setIsSingle(Boolean isSingle) {
		this.isSingle = isSingle;
	}

	public String getPubStatus() {
		return pubStatus;
	}

	public void setPubStatus(String pubStatus) {
		this.pubStatus = pubStatus;
	}

	public List<Chapter> getChapter() {
		return chapter;
	}

	public void setChapter(List<Chapter> chapter) {
		this.chapter = chapter;
	}
}
