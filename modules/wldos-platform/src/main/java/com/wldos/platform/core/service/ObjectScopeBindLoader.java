package com.wldos.platform.core.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import io.github.wldos.platform.support.rbac.ObjectScopeBindRegistry;

@Service
public class ObjectScopeBindLoader {

	private final JdbcOperations jdbcOperations;

	public ObjectScopeBindLoader(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	@PostConstruct
	public void load() {
		this.refresh();
	}

	public void refresh() {
		try {
			List<ObjectScopeBindRegistry.ObjectScopeBindRow> rows = this.jdbcOperations.query(
					"select entity_key, dimension_code, column_name from wo_object_scope_bind "
							+ "where delete_flag='normal' and is_valid='1'",
					(rs, i) -> new ObjectScopeBindRegistry.ObjectScopeBindRow(
							rs.getString("entity_key"),
							rs.getString("dimension_code"),
							rs.getString("column_name")));
			ObjectScopeBindRegistry.reload(rows);
		}
		catch (Exception ignored) {
			ObjectScopeBindRegistry.reload(java.util.Collections.singletonList(
					new ObjectScopeBindRegistry.ObjectScopeBindRow("WoUser", "com_id", "main_com_id")));
		}
	}
}
