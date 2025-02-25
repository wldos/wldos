/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.term.enums;

/**
 * 分类法枚举值。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
public enum TermTypeEnum {
	CATEGORY("category"),
	TAG("tag");

	private final String name;

	TermTypeEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}