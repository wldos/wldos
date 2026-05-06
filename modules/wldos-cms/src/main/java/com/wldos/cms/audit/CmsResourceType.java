/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.wldos.framework.support.audit.ResourceTypeDict;

/**
 * CMS 模块（{@code wldos-cms}）的操作日志资源类型枚举。
 *
 * <p>设计理念见 {@code com.wldos.platform.audit.resolver.PlatformResourceType}：
 * 用枚举集中声明本模块所有 type，避免 Map 字面量；新增 type 只改本类，
 * resolver 自动同步 {@code supportedResourceTypes()} 与 {@code resourceTypeLabels()}。
 *
 * <p>本模块的 {@code info / pub / book / chapter} 在物理上都映射到 {@code k_pubs} 一张表，
 * 通过 {@code k_pubs.pub_type} 列区分；{@code comment} 单独的 {@code k_comments} 表。
 *
 * @author 元悉宇宙
 * @date 2026/05/05
 * @version 1.0
 */
public enum CmsResourceType {

	/** 资讯/信息类内容（{@code k_pubs.pub_type='info'}）。 */
	INFO("info", "资讯"),

	/**
	 * 资讯封面附件（上传/替换 info 列表项的封面）——附属资源类型。
	 *
	 * <p>没有独立 ID → 名称的查表口径，{@code resourceId} 通常是父 info 的 id；
	 * lookupByType 直接返回空集，前端"资源"列保持空白即可。
	 */
	INFO_COVER("info_cover", "资讯封面"),

	/** 通用发布对象兜底（{@code k_pubs} 默认/聚合查询）。 */
	PUB("pub", "发布对象"),

	/** 书籍主对象（{@code k_pubs.pub_type='book'}）。 */
	BOOK("book", "书籍"),

	/** 书籍章节（{@code k_pubs.pub_type='chapter'}）。 */
	CHAPTER("chapter", "章节"),

	/**
	 * 章节插图附件（上传/替换章节正文图片）——附属资源类型，处理同 {@link #INFO_COVER}。
	 */
	CHAPTER_IMAGE("chapter_image", "章节插图"),

	/** 评论（{@code k_comments}），name 取评论内容前 30 字符做预览。 */
	COMMENT("comment", "评论"),

	/**
	 * 互动统计实体（{@code k_stars}：点赞/收藏/关注 的累计计数）。
	 *
	 * <p>StarController 走基类 CRUD 时 OpLogAspect 会自动推断出本 type；
	 * 互动表本身没有"业务名称"列，lookupByType 返回空集，前端"资源"列空白。
	 */
	STAR("star", "互动统计");

	private final String code;

	private final String label;

	CmsResourceType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public static Set<String> supportedCodes() {
		return SUPPORTED_CODES;
	}

	public static List<ResourceTypeDict> dict() {
		return DICT;
	}

	private static final Set<String> SUPPORTED_CODES;
	private static final List<ResourceTypeDict> DICT;
	static {
		CmsResourceType[] all = values();
		Set<String> codes = new HashSet<>(all.length * 2);
		List<ResourceTypeDict> dict = new ArrayList<>(all.length);
		for (CmsResourceType t : all) {
			codes.add(t.code);
			dict.add(new ResourceTypeDict(t.code, t.label));
		}
		SUPPORTED_CODES = Collections.unmodifiableSet(codes);
		DICT = Collections.unmodifiableList(dict);
	}
}
