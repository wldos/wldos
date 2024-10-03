/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.common.enums;

/**
 * 文件访问策略枚举。
 *
 * @author 元悉宇宙
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