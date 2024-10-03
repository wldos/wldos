/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.enums;

import com.wldos.common.Constants;

/**
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum OrgTypeEnum {

	PLATFORM("系统用户组", Constants.ENUM_TYPE_ORG_PLAT),
	ORG("组织机构", "org"),
	ROLE_ORG("角色组", "role_org"),
	TEAM("团队", "team"),
	GROUP("群组", "group"),
	CIRCLE("圈子", "circle");

	private final String label;

	private final String value;

	OrgTypeEnum(String label, String value) {
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