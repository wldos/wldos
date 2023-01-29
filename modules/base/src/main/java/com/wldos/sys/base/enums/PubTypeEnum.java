/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

import com.wldos.common.utils.ObjectUtils;

/**
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum PubTypeEnum {
	// 子类型：
	ATTACHMENT("attachment", null),
	CHAPTER("chapter", "attachment"),
	SERIES("series", null),
	// 基础类：
	POST("post", "attachment"),
	PIC("pic", null),
	PAGE("page", null),
	VIDEO("video", null),
	VOICE("voice", null),
	SERIES_VIDEO("series_video", "series"), // 系列视频，剧集
	SERIES_VOICE("series_voice", "series"), // 系列音频，剧集
	BOOK("book", "chapter"),
	PRODUCT("prod", null),
	SERVICE("service", null),
	TASK("task", null),
	INFO("info", null),
	// 扩展类：
	ANNUAL("annual", "chapter"), // 年谱
	CLASS("class", "series"), // 教程
	COOK("cook", "chapter"), // 菜谱
	MUSIC("music", "chapter"), // 乐谱
	MEDICAL("medical", "chapter"), // 药谱
	GENEALOGY("genealogy", "chapter"), // 族谱
	GAME("game", "chapter"), // 博弈谱
	SPORTS("sports", "chapter"), // 武术谱
	THEATRE("theatre", "chapter"), // 戏剧谱
	PAINT("paint", "chapter"), // 书画谱
	POETRY("poetry", "chapter"), // 诗词谱
	DANCE("dance", "chapter"), // 舞谱
	BIOLOGY("biology", "chapter"), // 生物谱
	FARM("farm", "chapter"), // 农业谱
	BUSINESS("business", "chapter"), // 商业谱
	NAV_ITEM_MENU("navItemMenu", null);

	private final String name;
	// 只对外展示主类型数据，子类型仅作为系统内部辅助
	private final String subType;

	PubTypeEnum(String name, String subType) {
		this.name = name;
		this.subType = subType;
	}

	public String getName() {
		return name;
	}

	public String getSubType() {
		return subType;
	}
	/** 获取所有子类型名称 */
	public static Object[] listSubType() {
		return new String[] {"chapter", "attachment", "series"};
	}

	@Override
	public String toString() {
		return this.name + (ObjectUtils.isBlank(this.subType) ? "" : " subType：" + this.subType);
	}
}