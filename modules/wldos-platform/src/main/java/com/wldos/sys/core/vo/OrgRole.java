/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.core.vo;

/**
 * 组织授予的角色。
 *
 * @author 元悉宇宙
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
