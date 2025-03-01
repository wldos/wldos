/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

/**
 * 角色类型枚举。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
public enum RoleTypeEnum {

	SYS_ROLE("sys_role"),
	SUBJECT("subject"),
	TAL_ROLE("tal_role");

	private final String roleName;

	RoleTypeEnum(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return this.roleName;
	}
}