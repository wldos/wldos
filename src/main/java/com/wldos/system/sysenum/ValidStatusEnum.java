/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 记录有效状态枚举。
 *
 * @Title ValidStatusEnum
 * @Package com.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum ValidStatusEnum {

	/** 有效。 */
	valid("1"),
	/** 无效 */
	invalid("0");

	private String enumName;

	ValidStatusEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}
