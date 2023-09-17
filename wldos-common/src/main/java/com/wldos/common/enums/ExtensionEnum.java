/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.common.enums;

/**
 * 扩展点枚举，声明所有启用的扩展点。类似于hook，但不是hook。
 *
 * @author 树悉猿
 * @date 2023/9/5
 * @version 1.0
 */
public enum ExtensionEnum {
	/** 发布内容扩展 */
	PUB_CONTENT("pubContent"),
	/** 待定扩展 */
	OTHER("other");

	private final String name;

	ExtensionEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
