/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.enums;

/**
 * 体系类型枚举。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public enum ArchTypeEnum {

	PLATFORM("platform"),

	ORG("org"),

	TEAM("team"),

	GROUP("group"),

	CIRCLE("circle");

	private final String roleName;

	ArchTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}
