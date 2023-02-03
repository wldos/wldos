/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.vo;

/**
 * 省分。
 *
 * @author 树悉猿
 * @date 2021/6/7
 * @version 1.0
 */
public class City {
	private Long id;

	private String name;

	private Long parentId;

	private String provName;

	public City() {
	}

	public City(Long id, String name, Long parentId, String provName) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.provName = provName;
	}

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
