/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.model;

/**
 * 系统统一内容模型key。
 * key前缀：PUB_、ATT_、EXT_分别对应公共扩展属性、附件扩展属性、自定义扩展属性。
 * 公共扩展属性：特点是全局唯一、确定。
 * 附件扩展属性：特点是一个实体可以拥有多个附件，表达附件扩展属性的key相同。
 * 自定义扩展属性：二次开发者追加扩展属性，特点是绑定具体的内容类型，由开发者决定何时输入、何时输出以及处理、展现的逻辑。
 *
 * @author 树悉猿
 * @date 2021/6/19
 * @version 1.0
 */
public final class KModelMetaKey {

	private KModelMetaKey() {
		throw new IllegalStateException("Utility class");
	}

	/** 知识对象副标题 */
	public static final String PUB_META_KEY_SUB_TITLE = "subTitle";

	/** 封面特色图path */
	public static final String PUB_META_KEY_COVER = "cover";

	/** 原价 */
	public static final String PUB_META_KEY_ORN_PRICE = "ornPrice";

	/** 现价 */
	public static final String PUB_META_KEY_PST_PRICE = "pstPrice";

	/** 联系人 */
	public static final String PUB_META_KEY_CONTACT = "contact";

	/** 联系电话 */
	public static final String PUB_META_KEY_TELEPHONE = "telephone";

	/** 所在省 */
	public static final String PUB_META_KEY_PROV = "province";

	/** 所在市 */
	public static final String PUB_META_KEY_CITY = "city";

	/** 所在区县 */
	public static final String PUB_META_KEY_COUNTY = "county";

	/** 查看数 */
	public static final String PUB_META_KEY_VIEWS = "views";

	/** 主图1 */
	public static final String PUB_META_KEY_MAIN_PIC1 = "mainPic1";

	/** 主图2 */
	public static final String PUB_META_KEY_MAIN_PIC2 = "mainPic2";

	/** 主图3 */
	public static final String PUB_META_KEY_MAIN_PIC3 = "mainPic3";

	/** 主图4 */
	public static final String PUB_META_KEY_MAIN_PIC4 = "mainPic4";

	/** 附件图片等 */
	public static final String ATT_META_KEY_ATTACH_PATH = "attachPath";

	public static final String ATT_META_KEY_ATTACH_METADATA = "attachMetadata";

	public static final String ATT_META_KEY_ATTACH_FILE_ALT = "attachFileAlt";

	// 自由扩展域
	public static final String EXT_META_KEY_EXT_ID = "id";

	public static final String EXT_META_KEY_EXT_KEY = "metaKey";

	public static final String EXT_META_KEY_EXT_NAME = "metaName";

	public static final String EXT_META_KEY_EXT_DATATYPE = "dataType";

	public static final String EXT_META_KEY_EXT_ENUM_VALUE = "enumValue";

	public static final String EXT_META_KEY_EXT_CONT_ID = "contentId";

	// 内容通配符前缀
	public static final String CONT_FILTER_SIGN_PREFIX = "wos-";

	// 侧边栏配置域
	public static final String SIDECAR_CONF_LIST_NUM = "pageSize";

	public static final String SIDECAR_CONF_TYPE = "postType";

	public static final String SIDECAR_CONF_SORTER = "sorter";

	public static final String SIDECAR_CONF_FILTER = "filter";

	public static final String SIDECAR_CONF_PAGENO = "current";
}
