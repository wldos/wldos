/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.wldos.common.enums.BaseEnum;

/**
 * 内容发布状态枚举值。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
@JsonFormat(shape = Shape.OBJECT)
public enum PubStatusEnum implements BaseEnum {
	/** 已发布(表)，只有主类型的作品可以有发布状态，作品的内容元素最多只能继承状态 */
	PUBLISH("已发布", "publish"),
	/** 继承, 子类型审核通过后为继承 */
	INHERIT("继承发布", "inherit"),
	/** 审核中：申请发布后进入待审核状态 */
	IN_REVIEW("待审核", "in_review"),
	/** 已下线，只有管理员可以下线 */
	OFFLINE("已下线", "offline"),
	/** 自己可见，管理员不可见 */
	PRIVATE("已隐藏", "private"),
	/** 初始化：用户新建动作触发编辑表单页，仅作状态交互不入库 */
	INITIAL("初始化", "initial"),
	/** 手动草稿：用户在新建的表单上做任何编辑操作，keyup时触发草稿保存入库 */
	DRAFT("手动草稿", "draft"),
	/** 自动草稿：自动保存，由用户设置是否开启，若开启定时保存 */
	AUTO_DRAFT("自动草稿", "auto_draft"),
	/** 回收站：已作废 */
	TRASH("已作废", "trash");

	private final String name;

	private final String value;

	PubStatusEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public String getLabel() {
		return this.name;
	}

	@Override
	public String getValue() {
		return this.value;
	}
}
