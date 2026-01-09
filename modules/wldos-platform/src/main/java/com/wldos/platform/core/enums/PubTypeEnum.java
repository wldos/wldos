/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.common.utils.ObjectUtils;

/**
 * 把各种类型放在一起方便系统地实现业务，世界是普遍联系的，分而治之已经走到尽头
 * 作品发布类型分为主类型和子类型，其中主类型又分单体类型、复合类型，子类型表示主类型的元素内容，主与子是父子关系，展现作品时以主类型为依据。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
public enum PubTypeEnum {
	// 子类型：作为内容存在，子类型为空
	ATTACHMENT("attachment", null, "附件"),
	CHAPTER("chapter", null, "元素"), // 暂定所有子类型都是chapter，属性和行为的不同根据主类型区别驱动
	// SERIES("series", null),

	// 单体类型：自身即内容
	POST("post", "post", "文章"),
	PIC("pic", "pic", "图片"),
	VIDEO("video", "video", "视频"),
	VOICE("voice", "voice", "音频"),
	INFO("info", "info", "信息"),
	DOC("doc", "chapter", "文档"), // 特指知识库文档
	PAGE("page", "page", "页面"),

	// 复合类型
	BOOK("book", "chapter", "文集"),
	ANNUAL("annual", "chapter", "年谱"), // 年谱
	CLASS("class", "chapter", "教程"), // 教程
	COOK("cook", "chapter", "菜谱"), // 菜谱
	MUSIC("music", "chapter", "乐谱"), // 乐谱
	MEDICAL("medical", "chapter", "药谱"), // 药谱
	GENEALOGY("genealogy", "chapter", "族谱"), // 族谱
	GAME("game", "chapter", "棋谱"), // 博弈谱
	SPORTS("sports", "chapter", "秘籍"), // 武术谱
	THEATRE("theatre", "chapter", "戏谱"), // 戏剧谱
	PAINT("paint", "chapter", "书画"), // 书画谱
	POETRY("poetry", "chapter", "诗词"), // 诗词谱
	DANCE("dance", "chapter", "舞谱"), // 舞谱

	/* @todo 暂时不支持
	PRODUCT("prod", "prod", "产品"),
	SERVICE("service", "service", "服务"),
	TASK("task", "task", "任务"),

	// 复合类型：作品包含系列内容

	SERIES_VIDEO("series_video", "chapter", "系列视频"), // 系列视频，剧集暂由chapter模型
	SERIES_VOICE("series_voice", "chapter", "系列音频"), // 系列音频，剧集暂由chapter模型

	// 复合扩展类：继承Book扩展，在扩展区拥有扩展属性和行为

	BIOLOGY("biology", "chapter", "生物"), // 生物谱
	FARM("farm", "chapter", "农业"), // 农业谱
	BUSINESS("business", "chapter", "商业")*/; // 商业谱


	private final String name;

	// 只对外展示主类型数据，子类型仅作为系统内部辅助
	private final String subType;

	// 名称
	private final String label;

	PubTypeEnum(String name, String subType, String label) {
		this.name = name;
		this.subType = subType;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public String getSubType() {
		return subType;
	}

	public String getLabel() {
		return label;
	}

	/** 获取所有子类型名称 */
	public static List<String> listSubType() {
		return Arrays.stream(PubTypeEnum.values()).filter(pubType -> pubType.subType == null).map(PubTypeEnum::getName).collect(Collectors.toList());
	}

	/** 获取所有单体类型名称 */
	public static List<String> listSingleType() {
		return Arrays.stream(PubTypeEnum.values()).filter(pubType -> pubType.name.equals(pubType.subType)).map(PubTypeEnum::getName).collect(Collectors.toList());
	}

	/** 获取所有主类型名称，具体应用中parent_id为0的都是主类型 */
	public static List<String> listMainType() {
		return Arrays.stream(PubTypeEnum.values()).filter(pubType -> pubType.subType != null).map(PubTypeEnum::getName).collect(Collectors.toList());
	}

	/** 获取不含页面page类型的所有主类型 */
	public static List<String> listMainTypeNoPage() {
		return Arrays.stream(PubTypeEnum.values()).filter(pubType -> pubType.subType != null && !pubType.name.equals("page")).map(PubTypeEnum::getName).collect(Collectors.toList());
	}

	/**
	 * 取出复合类型的子类型
	 *
	 * @param complexTypeName 复合类型名
	 * @return 子类型名称，如果不是复合类型，返回null
	 */
	public static String takeSubType(String complexTypeName) {
		PubTypeEnum complexType = PubTypeEnum.valueOfName(complexTypeName);
		if (PubTypeEnum.isComplex(complexType))
			return complexType.subType;
		return null;
	}

	/**
	 * 判断某发布类型是否单体类型
	 *
	 * @param pubType 发布类型枚举对象
	 * @return 是否
	 */
	public static boolean isSingle(String pubType) {
		return PubTypeEnum.isSingle(PubTypeEnum.valueOfName(pubType));
	}

	/**
	 * 判断某发布类型是否单体类型
	 *
	 * @param pubType 发布类型枚举对象
	 * @return 是否
	 */
	public static boolean isSingle(PubTypeEnum pubType) {
		return pubType.name.equals(pubType.subType);
	}

	public static boolean isComplex(String pubType) {
		PubTypeEnum curType = PubTypeEnum.valueOfName(pubType);
		return curType.subType != null && !curType.name.equals(curType.subType);
	}

	/**
	 * 判断某发布类型是否复合类型
	 *
	 * @param pubType 发布类型枚举对象
	 * @return 是否
	 */
	public static boolean isComplex(PubTypeEnum pubType) {
		return pubType.subType != null && !pubType.name.equals(pubType.subType);
	}

	/**
	 * 判断某发布类型是否子类型
	 *
	 * @param pubType 发布类型枚举对象
	 * @return 是否
	 */
	public static boolean isSub(PubTypeEnum pubType) {
		return pubType.subType == null;
	}

	/**
	 * 判断某发布类型是否主类型
	 *
	 * @param pubType 发布类型枚举对象
	 * @return 是否
	 */
	public static boolean isMainType(PubTypeEnum pubType) {
		return pubType.subType != null;
	}

	/**
	 * 判断某发布类型是否子类型
	 *
	 * @param pubType 发布类型
	 * @return 是否
	 */
	public static boolean isSub(String pubType) {
		return PubTypeEnum.valueOfName(pubType).subType == null;
	}

	/**
	 * 判断某发布类型是否主类型
	 *
	 * @param pubType 发布类型
	 * @return 是否
	 */
	public static boolean isMainType(String pubType) {
		return PubTypeEnum.valueOfName(pubType).subType != null;
	}

	/**
	 * 通过小写name获取枚举
	 *
	 * @param name 枚举name属性
	 */
	public static PubTypeEnum valueOfName(String name) {
		return Enum.valueOf(PubTypeEnum.class, name.toUpperCase());
	}

	@Override
	public String toString() {
		return this.name + (ObjectUtils.isBlank(this.subType) ? "" : " subType：" + this.subType);
	}
}