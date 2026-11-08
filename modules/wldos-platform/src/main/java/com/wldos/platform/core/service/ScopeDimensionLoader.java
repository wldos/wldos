/**
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import io.github.wldos.platform.support.rbac.ScopeDimensionCode;
import io.github.wldos.platform.support.rbac.ScopeDimensionRegistry;

/**
 * 范围维加载器。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class ScopeDimensionLoader {

	private final JdbcOperations jdbcOperations;

	public ScopeDimensionLoader(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	@PostConstruct
	public void load() {
		this.refresh();
	}

	public void refresh() {
		try {
			List<ScopeDimensionRegistry.ScopeDimensionRow> rows = this.jdbcOperations.query(
					"select dimension_code, value_source, match_mode from wo_scope_dimension "
							+ "where delete_flag='normal' and is_valid='1' and enabled='1' order by sort_order",
					(rs, i) -> new ScopeDimensionRegistry.ScopeDimensionRow(
							rs.getString("dimension_code"),
							rs.getString("value_source"),
							rs.getString("match_mode")));
			ScopeDimensionRegistry.reload(rows);
		}
		catch (Exception ignored) {
			ScopeDimensionRegistry.reload(java.util.Collections.singletonList(
					new ScopeDimensionRegistry.ScopeDimensionRow(
							ScopeDimensionCode.ORG_ID.getValue(), "ORG_TREE", "SUBTREE")));
		}
	}
}
