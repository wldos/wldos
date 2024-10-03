/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.auth.vo;

import com.wldos.auth.model.ModifyParams;

/**
 * 密保手机修改参数。
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
public class MobileModifyParams implements ModifyParams {
	/** 登录用户id */
	private Long id;

	private String oldMobile;

	private String mobile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOldMobile() {
		return oldMobile;
	}

	public void setOldMobile(String oldMobile) {
		this.oldMobile = oldMobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String getOld() {
		return this.oldMobile;
	}

	@Override
	public String getNew() {
		return this.mobile;
	}
}
