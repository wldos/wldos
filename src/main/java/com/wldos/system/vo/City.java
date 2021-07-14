/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.vo;

/**
 * 省分。
 *
 * @Title Prov
 * @Package com.wldos.system.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/6/7
 * @Version 1.0
 */
public class City {
	private Long id;

	private String name;

	private Long parentId;

	private String provName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}
}
