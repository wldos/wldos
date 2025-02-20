/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.enums;

/**
 * 记录删除标志枚举。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum DeleteFlagEnum {

	/** 有效。 */
	NORMAL("normal"),
	/** 无效 */
	DELETED("deleted");

	private final String enumName;

	DeleteFlagEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}