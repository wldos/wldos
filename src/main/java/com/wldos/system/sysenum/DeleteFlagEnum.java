/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 记录删除标志枚举。
 *
 * @Title DeleteFlagEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum DeleteFlagEnum {

	/** 有效。 */
	normal("normal"),
	/** 无效 */
	deleted("deleted");

	private String enumName;

	DeleteFlagEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}
