/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.vo;

/**
 * 组织授予的角色。
 *
 * @Title OrgRole
 * @Package com.wldos.system.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/21
 * @Version 1.0
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
