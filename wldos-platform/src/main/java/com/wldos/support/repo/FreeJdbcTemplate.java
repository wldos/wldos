/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.repo;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.cms.dto.LevelNode;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.NameConvert;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;

/**
 * 实现自由的jdbc操作，支持实体级的CRUD 和 自定义复杂SQL，定义公共jdbc操作模板。
 *
 * @author 树悉猿
 * @date 2021-04-16
 * @version V1.0
 */
@SuppressWarnings("unchecked")
@Slf4j
public class FreeJdbcTemplate {

	@Autowired
	private JdbcAggregateTemplate jdbcAggTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	public JdbcAggregateTemplate getJdbcAggTemplate() {
		return jdbcAggTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParamJdbcTemplate() {
		return namedParamJdbcTemplate;
	}

	public <E> E queryForObject(Class<E> clazz, String sql, Object[] params) {

		return this.getNamedParamJdbcTemplate().getJdbcOperations().queryForObject(sql, new BeanPropertyRowMapper<>(clazz), params);
	}

	public List<Map<String, Object>> execQueryForPage(String sql, int currentPage, int pageSize, Object[] params) {

		int startRow = (currentPage - 1) * pageSize;
		return this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList("select  A.* from ( " + sql + " ) A limit " + startRow + "," + pageSize, params);
	}

	public <E> List<E> execQueryForPage(Class<E> clazz, String sql, int currentPage, int pageSize, Object ...params) {

		int startRow = (currentPage - 1) * pageSize;
		StringBuilder mysql = new StringBuilder(200);
		mysql.append("select  A.* from ( ").append(sql).append(" ) A limit ").append(startRow).append(",").append(pageSize);
		return this.getNamedParamJdbcTemplate().getJdbcOperations().query(mysql.toString(), new BeanPropertyRowMapper<>(clazz), params);
	}

	private String getTotalSql(String sql) {
		return "select count(1) as total from ( " + sql + " )";
	}

	public PageableResult<Map<String, Object>> execQueryForPageNoOrder(String sql, int currentPage, int pageSize, Object[] params) {

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<Map<String, Object>> list = this.execQueryForPage(sql, currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	public <E> PageableResult<E> execQueryForPageNoOrder(Class<E> clazz, String sql, int currentPage, int pageSize, Object[] params) {

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<E> list = this.execQueryForPage(clazz, sql, currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	public PageableResult<Map<String, Object>> execQueryForPage(String sql, String sqlOrder, int currentPage, int pageSize, Object[] params) {

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<Map<String, Object>> list = this.execQueryForPage(sql+ObjectUtil.string(sqlOrder), currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	public <E> PageableResult<E> execQueryForPage(Class<E> clazz, String sql, String sqlOrder, PageQuery pageQuery, Object ...params) {

		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<E> list = this.execQueryForPage(clazz, sql+ObjectUtil.string(sqlOrder), currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	public <E> String getTableNameByEntity(Class<E> clazz) {
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}

		return tableName;
	}

	public <E> String getIdColNameByEntity(E entity) {
		Class<E> clazz = this.clazz(entity);

		return this.getIdColNameByEntity(clazz);
	}

	public <E> String getIdColNameByEntity(Class<E> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		String pKeyName = "";

		for (Field f : fields) {
			if (f.getAnnotation(Id.class) != null) {
				Column propRelColumn = f.getAnnotation(Column.class);
				if (propRelColumn != null) {
					pKeyName = propRelColumn.value();
				}
				else {
					pKeyName = NameConvert.humpToUnderLine(f.getName());
				}
				break;
			}
		}

		return pKeyName;
	}

	public <E> List<String> getMultiIdColNamesByEntity(Class<E> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		List<String> pKeyNames = new ArrayList<>();

		for (Field f : fields) {
			if (f.getAnnotation(Id.class) != null) {
				Column propRelColumn = f.getAnnotation(Column.class);
				if (propRelColumn != null) {
					pKeyNames.add(propRelColumn.value());
				}
				else {
					pKeyNames.add(NameConvert.humpToUnderLine(f.getName()));
				}
			}
		}

		return pKeyNames;
	}

	public <E> Class<E> clazz(E entity) {
		Class<E> clazz = null;
		try {
			clazz = (Class<E>) Class.forName(entity.getClass().getName());
		}
		catch (ClassNotFoundException e) {
			throw new JdbcToolsException("未找到类：", e);
		}
		return clazz;
	}

	public StringBuilder querySqlByTable(String tableName, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
		StringBuilder sql = new StringBuilder(50).append("select a.* from ").append(tableName).append(" ").append("a").append(" where 1=1 ");

		Field[] fields = entity.getDeclaredFields();

		if (!ObjectUtil.isBlank(condition)) {

			List<Field> fieldList = this.fieldsMatchCondition(condition, fields);
			StringBuilder querySql = new StringBuilder();
			fieldList.forEach(f -> {
				String key = f.getName();
				Object value = condition.get(key);
				Field field = null;
				try {
					field = ReflectionUtils.findRequiredField(entity, key);
				} catch (Exception e) {}
				if (field == null)
					return;
				if (field.getType().equals(String.class)) {
					querySql.append(" and instr(a.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) {
					querySql.append(" and DATE_FORMAT(a.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
				}
				else {
					querySql.append(" and a.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
				}
				params.add(value);
			});
			sql.append(querySql);
		}

		if (!ObjectUtil.isBlank(filter)) {
			StringBuilder finalSql = new StringBuilder();
			List<Field> fieldList = this.fieldsMatchFilter(filter, fields);
			fieldList.forEach(f -> {
				String key = f.getName();
				List<Object> value = filter.get(key);

				finalSql.append(" and a.").append(NameConvert.humpToUnderLine(key)).append(" in (");
				value.forEach(item -> {
					finalSql.append("?,");
					params.add(item);
				});
				if (finalSql.length() > 0) {
					finalSql.delete(finalSql.length() - 1, finalSql.length());

					finalSql.append(")");
				}
			});

			sql.append(finalSql);
		}

		return sql;
	}

	private List<Field> fieldsMatchFilter( Map<String, List<Object>> filter, Field[] fields) {
		return filter.entrySet().parallelStream().filter(p -> p.getValue() != null).map(item -> {
			String key = item.getKey();
			for (Field f : fields) {
				if (key.equals(f.getName())) {
					return f;
				}
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private List<Field> fieldsMatchCondition( Map<String, Object> condition, Field[] fields) {
		return condition.entrySet().parallelStream().filter(p -> p.getValue() != null).map(item -> {
			String key = item.getKey();
			for (Field f : fields) {
				if (key.equals(f.getName())) {
					return f;
				}
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void orderSql(Sort sort, @NonNull StringBuilder querySql) {
		if (sort != null && !sort.isEmpty()) {
			StringBuilder temp = new StringBuilder(" order by ");
			sort.stream().iterator().forEachRemaining(s ->
					temp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "" : " desc "));
			querySql.append(temp);
		}
	}

	public String existsSql(String cTable, String cAlias, String pIdKey, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {

		if (ObjectUtil.isBlank(condition)) {
			return "";
		}

		Field[] fields = entity.getDeclaredFields();
		StringBuilder existsSql = new StringBuilder(" and exists(select ").append(cAlias).append(".id from ").append(cTable).append(" ").append(cAlias).append(" where ")
				.append(cAlias).append(".").append(pIdKey).append("=a.id");
		int start = params.size();
		if (!ObjectUtil.isBlank(condition)) {

			List<Field> fieldList = this.fieldsMatchCondition(condition, fields);
			fieldList.forEach(f -> {
				String key = f.getName();
				Object value = condition.get(key);
				Field field = null;
				try {
					field = ReflectionUtils.findRequiredField(entity, key);
				} catch (Exception e) {}
				if (field == null)
					return;
				if (field.getType().equals(String.class)) {
					existsSql.append(" and instr(").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) {
					existsSql.append(" and date_format(").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = date_format(?, '%Y-%m-%d') ");
				}
				else {
					existsSql.append(" and ").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append("= ? ");
				}
				params.add(value);
			});
		}

		if (!ObjectUtil.isBlank(filter)) {
			StringBuilder finalSql = new StringBuilder();
			List<Field> fieldList = this.fieldsMatchFilter(filter, fields);
			fieldList.forEach(f -> {
				String key = f.getName();
				List<Object> value = filter.get(key);

				finalSql.append(" and ").append(cAlias).append(".").append(NameConvert.humpToUnderLine(key)).append(" in (");
				value.forEach(item -> {
					finalSql.append("?,");
					params.add(item);
				});
				if (finalSql.length() > 0) {
					finalSql.delete(finalSql.length() - 1, finalSql.length());

					finalSql.append(")");
				}
			});

			existsSql.append(finalSql);
		}

		if (params.size() - start > 0) {
			existsSql.append(")");
			return existsSql.toString();
		}

		return "";
	}

	public List<LevelNode> queryTreeByParentId(String table, Object pId) {
		StringBuilder sql = new StringBuilder("SELECT idt.level, target.id FROM (SELECT @ids AS _ids, (SELECT @ids := GROUP_CONCAT(id) FROM ")
				.append(table).append(" WHERE FIND_IN_SET(parent_id, @ids) ) as cIds, @lv := @lv+1 as level FROM ")
				.append(table).append(", (SELECT @ids :=?, @lv := 0 ) b WHERE @ids IS NOT NULL ) idt, ")
				.append(table).append(" target WHERE FIND_IN_SET(target.id, idt._ids) ORDER BY level, id");

		return this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(LevelNode.class), pId);
	}

	public List<LevelNode> queryTreeByChildId(String table, Object cId) {
		StringBuilder sql = new StringBuilder("SELECT idt.level, target.id FROM (SELECT @id as _id, (SELECT @id := parent_id FROM ")
		.append(table).append(" WHERE id = @id ) as _pid, @l := @l+1 as level FROM ")
				.append(table).append(",(SELECT @id := ?, @l := 0 ) b WHERE @id > 0 ) idt, ")
		.append(table).append(" target WHERE idt._id = target.id ORDER BY level;");

		return this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(LevelNode.class), cId);
	}
}
