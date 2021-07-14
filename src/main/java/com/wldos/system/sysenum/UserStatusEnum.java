/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 用户状态枚举。
 *
 * @Title UserStatusEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum UserStatusEnum {

	/** 有效。 */
	normal("normal"),
	/** 无效 */
	cancelled("cancelled"),
	/** 已锁定 */
	locked("locked");

	private String enumName;

	UserStatusEnum(String enumName) {
		this.enumName = enumName;
	}

	@Override
	public String toString() {
		return this.enumName;
	}
}
