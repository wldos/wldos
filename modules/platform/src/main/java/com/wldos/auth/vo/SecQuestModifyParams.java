/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.vo;

import com.wldos.auth.model.ModifyParams;

/**
 * 密保问题修改参数。
 *
 * @author 树悉猿
 * @date 2022/5/21
 * @version 1.0
 */
public class SecQuestModifyParams implements ModifyParams {
	/** 登录用户id */
	private Long id;

	private String oldSecQuest;

	private String secQuest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOldSecQuest() {
		return oldSecQuest;
	}

	public void setOldSecQuest(String oldSecQuest) {
		this.oldSecQuest = oldSecQuest;
	}

	public String getSecQuest() {
		return secQuest;
	}

	public void setSecQuest(String secQuest) {
		this.secQuest = secQuest;
	}

	@Override
	public String getOld() {
		return this.oldSecQuest;
	}

	@Override
	public String getNew() {
		return this.secQuest;
	}
}
