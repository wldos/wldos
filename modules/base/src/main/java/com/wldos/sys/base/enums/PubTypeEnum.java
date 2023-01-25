/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum PubTypeEnum {
	POST("post"),
	PAGE("page"),
	BOOK("book"),
	CHAPTER("chapter"),
	PRODUCT("prod"),
	SERVICE("service"),
	TASK("task"),
	INFO("info"),
	ANNUAL("annual"), // 年谱
	CLASS("class"), // 教程
	COOK("cook"), // 菜谱
	MUSIC("music"), // 乐谱
	MEDICAL("medical"), // 药谱
	GENEALOGY("genealogy"), // 族谱
	GAME("game"), // 博弈谱
	SPORTS("sports"), // 武术谱
	THEATRE("theatre"), // 戏剧谱
	PAINT("paint"), // 书画谱
	POETRY("poetry"), // 诗词谱
	DANCE("dance"), // 舞谱
	BIOLOGY("biology"), // 生物谱
	FARM("farm"), // 农业谱
	BUSINESS("business"), // 商业谱
	ATTACHMENT("attachment"),
	NAV_ITEM_MENU("navItemMenu");

	private final String name;

	PubTypeEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}