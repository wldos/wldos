/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
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
	STRONG("strong", "ok"),
	/** 中。 */
	MEDIUM("medium", "pass"),
	/** 弱。 */
	POOR("weak", "poor");

	private final String level;
	private final String status;

	PasswdStatusEnum(String level, String status) {
		this.level = level;
		this.status = status;
	}

	public String getLevel() {
		return level;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "level: " + this.level + " status: " + this.status;
	}
}
