/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.enums;

/**
 * 密码强度枚举。
 *
 * @author 元悉宇宙
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
