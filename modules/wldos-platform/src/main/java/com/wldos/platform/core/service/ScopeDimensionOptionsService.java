/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.ScopeDimensionDao;
import com.wldos.platform.core.entity.WoScopeDimension;

import io.github.wldos.common.vo.SelectOption;

/**
 * 范围维 ValueSource 选项解析（角色管理 UI 动态数据源）。
 *
 * @author Yuanxi Universe
 * @version 1.0.0
 * @since 3.0
 */
@Service
public class ScopeDimensionOptionsService extends NonEntityService {

	private final ScopeDimensionDao scopeDimensionDao;
	private final JdbcOperations jdbcOperations;

	@Autowired
	@Lazy
	private DictService dictService;

	public ScopeDimensionOptionsService(ScopeDimensionDao scopeDimensionDao, JdbcOperations jdbcOperations) {
		this.scopeDimensionDao = scopeDimensionDao;
		this.jdbcOperations = jdbcOperations;
	}

	public List<SelectOption> resolveOptions(String dimensionCode) {
		WoScopeDimension dim = this.scopeDimensionDao.findByDimensionCode(dimensionCode);
		if (dim == null || !"1".equals(dim.getEnabled())) {
			return Collections.emptyList();
		}
		String source = dim.getValueSource() == null ? "" : dim.getValueSource().trim();
		if ("ORG_TREE".equals(source)) {
			return Collections.emptyList();
		}
		if (source.startsWith("TABLE:")) {
			return loadTableOptions(source.substring("TABLE:".length()));
		}
		if (source.startsWith("DICT:")) {
			return this.dictService.select(source.substring("DICT:".length()));
		}
		if (source.startsWith("TERM_CLASS:")) {
			return this.dictService.select(source.substring("TERM_CLASS:".length()));
		}
		if (source.startsWith("TERM_TYPE:")) {
			return loadTermTypeOptions(source.substring("TERM_TYPE:".length()));
		}
		return Collections.emptyList();
	}

	private List<SelectOption> loadTableOptions(String tableSpec) {
		if (tableSpec.startsWith("wo_region:")) {
			return loadRegionLevelOptions(tableSpec.substring("wo_region:".length()));
		}
		if ("wo_region".equalsIgnoreCase(tableSpec)) {
			return loadRegionLevelOptions(null);
		}
		return Collections.emptyList();
	}

	private List<SelectOption> loadRegionLevelOptions(String level) {
		StringBuilder sql = new StringBuilder(
				"select region_code, name from wo_region where delete_flag='normal' and is_valid='1' ");
		List<Object> params = new ArrayList<>();
		if (level != null && !level.isEmpty()) {
			sql.append("and level = ? ");
			params.add(level);
		}
		sql.append("order by display_order, region_code limit 5000");
		List<Map<String, Object>> rows = params.isEmpty()
				? this.jdbcOperations.queryForList(sql.toString())
				: this.jdbcOperations.queryForList(sql.toString(), params.toArray());
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOption> options = new ArrayList<>(rows.size());
		for (Map<String, Object> row : rows) {
			Object code = row.get("region_code");
			Object name = row.get("name");
			if (code == null) {
				continue;
			}
			options.add(SelectOption.of(String.valueOf(name), String.valueOf(code)));
		}
		return options;
	}

	private List<SelectOption> loadTermTypeOptions(String termTypeId) {
		if (!StringUtils.hasText(termTypeId)) {
			return Collections.emptyList();
		}
		Long typeId;
		try {
			typeId = Long.parseLong(termTypeId.trim());
		} catch (NumberFormatException ex) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> rows = this.jdbcOperations.queryForList(
				"select t.id, t.name, t.slug from k_terms t inner join k_term_type tt on tt.term_id = t.id "
						+ "where tt.id = ? and t.delete_flag = 'normal' and t.is_valid = '1' "
						+ "order by t.display_order, t.id limit 500",
				typeId);
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}
		return rows.stream()
				.map(row -> {
					Object slug = row.get("slug");
					Object id = row.get("id");
					String value = slug != null && StringUtils.hasText(String.valueOf(slug))
							? String.valueOf(slug)
							: String.valueOf(id);
					return SelectOption.of(String.valueOf(row.get("name")), value);
				})
				.collect(Collectors.toList());
	}
}
