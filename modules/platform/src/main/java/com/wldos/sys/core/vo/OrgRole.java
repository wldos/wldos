/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.vo;

/**
 * 组织授予的角色。
 *
 * @author 树悉猿
 * @date 2021/5/21
 * @version 1.0
 */
public class OrgRole {
	// 角色id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return "{id: " + id.toString() + "}";
	}
}
