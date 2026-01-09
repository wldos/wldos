/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.plugins.core;

/**
 * 插件自启状态枚举。
 *
 * @author 元悉宇宙
 * @date 2025/4/27
 * @version 1.0
 */
public enum AutoStartEnum {

	/** 自动。 */
	AutoStart("1", "自动"),
	/** 手动 */
	HandStart("0", "手动");

	private final String enumName;
	private final String description;

	AutoStartEnum(String enumName, String description) {
		this.enumName = enumName;
		this.description = description;
	}

	@Override
	public String toString() {
		return this.enumName;
	}

	public String getEnumName() {
		return enumName;
	}

	public String getDescription() {
		return description;
	}
}