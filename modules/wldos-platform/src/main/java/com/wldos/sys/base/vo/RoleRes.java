/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.vo;

/**
 * 角色授予的资源。
 *
 * @author 元悉宇宙
 * @date 2021/5/21
 * @version 1.0
 */
public class RoleRes {
	// 资源id
	private Long id;

	private boolean inherit;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isInherit() {
		return inherit;
	}

	public void setInherit(boolean inherit) {
		this.inherit = inherit;
	}

	public String toString() {
		return "{id: " + id.toString() + ", inherit: " + inherit + "}";
	}
}
