/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.dto;

/**
 * 分级树节点。用于在数据库中查询指定父节点的所有字节点，包含父节点本身作为根节点。
 *
 * @author 树悉猿
 * @date 2021/8/29
 * @version 1.0
 */
public class LevelNode {

	private Integer level;

	private Long id;

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}