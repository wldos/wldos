/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.enums;

/**
 * 密码强度枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum PasswdStatusEnum {

	/** 强。 */
	STRONG("strong"),
	/** 中。 */
	MEDIUM("medium"),
	/** 弱。 */
	POOR("weak");

	private final String name;

	PasswdStatusEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
