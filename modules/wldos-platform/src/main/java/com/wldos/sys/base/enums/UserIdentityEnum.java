/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.enums;

/**
 * 平台保留用户身份相关枚举值。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum UserIdentityEnum {
	ORG("org"),
	COM("com"),
	SOCIAL("social"),
	MIDDLE("middle"),
	SERVANT("servant");

	private final String identityName;

	UserIdentityEnum(String identityName) {
		this.identityName = identityName;
	}

	@Override
	public String toString() {
		return this.identityName;
	}
}