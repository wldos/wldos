/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.vo;

/**
 * 发布同一内容的成员。
 *
 * @author 树悉猿
 * @date 2021/6/20
 * @version 1.0
 */
public class PubMember {
	private Long pubId;

	private Long id;

	private String nickname;

	private String avatar;

	public Long getPubId() {
		return pubId;
	}

	public void setPubId(Long pubId) {
		this.pubId = pubId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
