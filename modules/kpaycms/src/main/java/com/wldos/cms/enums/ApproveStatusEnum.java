/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.enums;

/**
 * 审批状态枚举值。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
public enum ApproveStatusEnum {
	/** 待审批 */
	APPROVING("待审批", "0"),
	/** 审批通过 */
	APPROVED("审批通过", "1"),
	/** 垃圾信息 */
	SPAM("垃圾信息", "spam"),
	/** 回收站 */
	TRASH("回收站", "trash");

	private final String label;

	private final String value;

	ApproveStatusEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}
