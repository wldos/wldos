/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.enums;

/**
 * 内容发布状态枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum PubStatusEnum {
	/** 已发布(表) */
	PUBLISH("publish"),
	/** 继承 */
	INHERIT("inherit"),
	/** 审核中：申请发布 */
	IN_REVIEW("in_review"),
	/** 已下线 */
	OFFLINE("offline"),
	/** 初始化：用户新建动作触发编辑表单页，仅作状态交互不入库 */
	INITIAL("initial"),
	/** 手动草稿：用户在新建的表单上做任何编辑操作，keyup时触发草稿保存入库 */
	DRAFT("draft"),
	/** 自动草稿：自动保存，由用户设置是否开启，若开启定时保存 */
	AUTO_DRAFT("auto_draft"),
	/** 回收站：已作废 */
	TRASH("trash");

	private final String name;

	PubStatusEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
