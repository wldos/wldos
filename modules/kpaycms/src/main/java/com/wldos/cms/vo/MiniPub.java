/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 发布内容链接相关信息。
 *
 * @author 树悉猿
 * @date 2021/8/23
 * @version 1.0
 */
public class MiniPub {
	private Long id;

	private String pubTitle;

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
}
