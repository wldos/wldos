/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.vo;

/**
 * 角色授予的资源。
 *
 * @Title AuthRole
 * @Package com.wldos.system.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/21
 * @Version 1.0
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
