/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.enums;

/**
 * 内容模型类型枚举。用于区分不同的数据内容模型，暂以帖子形式实现，不同类型具备不同的业务对象，表现为属性、行为、业务场景和展现样式等不同，本质上
 * 一类模型对应一种子系统， 如post表示数据来自博客文章系统，book表示数据来自内容创作系统，info表示数据来自信息发布系统。
 * 单篇与作品的区别在于，一个是文章，一个是书，他们都不限内容表现形式，可以包含图文、多媒体。
 * 所以，4中内容模板只是用来表达不同的内容格式及其渲染方式，在应用层面上不存在4种，而是以章节和作品形式存在的各种内容，专业不同，内容
 * 的内涵不同，可能是博客，可能是年谱，可能是教程视频。
 * 那么，对于不同专业的内容，平台提供基于创作工作台的一定的自由度，可以在表单上划分一块自定义模块，由用户根据专业需要定义字段名称和值，
 * 这些自定义变量可以在详情页展示，至于更多的扩展场景，后期根据需要演进。
 * 基于行业门类、属性、模板和行为逻辑的扩展，交给插件化实现，因为数据库变了，原有的程序必须打补丁驱动他们，这个补丁由开发者实现为插件，
 * 也就是后期平台着重要封装的插件化hook机制。
 * 2022/01/10增加信息类型INFO，info专指供求信息类型，用于信息增值服务。
 *
 * 一类内容模型对应一种子系统，与其他模型共享一份行业门类和类型细分(分类目录)。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum ContModelTypeEnum {
	/** 单独发表的文章 */
	POST("post"),
	/** 页面 */
	PAGE("page"),
	/** 作品 */
	BOOK("book"),
	/** 作品的内容(章节) */
	CHAPTER("chapter"),
	/** 产品 */
	PRODUCT("prod"),
	/** 服务：线上服务 */
	SERVICE("service"),
	/** 任务：众包任务 */
	TASK("task"),
	/** 供求信息 */
	INFO("info"),
	/** 附件 */
	ATTACHMENT("attachment"),
	/** 分类导航菜单: 实现基于行业门类管理的分类导航 */
	NAV_ITEM_MENU("navItemMenu");

	private final String name;

	ContModelTypeEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
