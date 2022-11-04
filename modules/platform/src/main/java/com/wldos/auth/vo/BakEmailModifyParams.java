/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.vo;

import com.wldos.auth.model.ModifyParams;

/**
 * 备用邮箱修改参数。
 *
 * @author 树悉猿
 * @date 2022/5/21
 * @version 1.0
 */
public class BakEmailModifyParams implements ModifyParams {
	/** 登录用户id */
	private Long id;

	private String oldBakEmail;

	private String bakEmail;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOldBakEmail() {
		return oldBakEmail;
	}

	public void setOldBakEmail(String oldBakEmail) {
		this.oldBakEmail = oldBakEmail;
	}

	public String getBakEmail() {
		return bakEmail;
	}

	public void setBakEmail(String bakEmail) {
		this.bakEmail = bakEmail;
	}

	@Override
	public String getOld() {
		return this.oldBakEmail;
	}

	@Override
	public String getNew() {
		return this.bakEmail;
	}
}
