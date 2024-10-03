/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.cms.enums;

/**
 * 作品列表样式枚举值。
 *
 * @author 元悉宇宙
 * @date 2023/1/27
 * @version 1.0
 */
public enum ListStyleEnum {
	/** 卡片式：以特色图为背景的瀑布流或一组卡片 */
	card("card"),
	/** 存档式：图文列表 */
	archive("archive");

	private final String name;

	ListStyleEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
