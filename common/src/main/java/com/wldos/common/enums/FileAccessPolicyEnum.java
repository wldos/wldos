/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.enums;

/**
 * 文件访问策略枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum FileAccessPolicyEnum {

	/** 公开访问 */
	PUBLIC("store"),
	/** 私密文件 */
	PRIVATE("oss");

	private final String enumName;

	FileAccessPolicyEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}
