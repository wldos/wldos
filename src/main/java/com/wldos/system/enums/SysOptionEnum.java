/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 系统配置选项枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum SysOptionEnum {

	/** 新注册用户默认属组：普通会员组，对应普通会员角色。 */
	DEFAULT_GROUP("default_group"),
	/** other */
	OTHER("other");

	private final String roleName;

	SysOptionEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
