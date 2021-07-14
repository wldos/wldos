/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.sysenum;

/**
 * 系统配置选项枚举。
 *
 * @Title SysOptionEnum
 * @Package com.wldos.system.sysenum
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public enum SysOptionEnum {

	/** 新注册用户默认属组：普通会员组，对应普通会员角色。 */
	defaultGroup("default_group"),
	/** other */
	other("other");

	private String roleName;

	SysOptionEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
