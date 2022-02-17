/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.dto;

/**
 * sql中出现的table。
 *
 * @author 树悉猿
 * @date 2021/12/28
 * @version 1.0
 */
public class SQLTable {
	private final String tableName;

	private final String alias;
	/** 实体类型 */
	private final Class<?> type;

	public SQLTable(String tableName, String alias, Class<?> type) {
		this.tableName = tableName;
		this.alias = alias;
		this.type = type;
	}

	public String getTableName() {
		return tableName;
	}

	public String getAlias() {
		return alias;
	}

	public Class<?> getType() {
		return type;
	}
}
