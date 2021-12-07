/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.repo.FreeJdbcTemplate;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.vo.TreeNode;
import com.wldos.support.util.NameConvert;
import com.wldos.support.util.TreeUtil;
import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

/**
 * 公共顶层jdbc操作实现类。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@Component
public class CommonJdbcOperation extends FreeJdbcTemplate {

	public <E> PageableResult<E> execQueryForPage(Class<E> clazz, PageQuery pageQuery) {

		return this.execQueryForPage(clazz, pageQuery, clazz, true);
	}

	public <V extends TreeNode<V>, E> PageableResult<V> execQueryForTree(Class<?> clazz, PageQuery pageQuery, Class<E> entity, long root) {
		PageableResult<V> voPageableResult = this.execQueryForPage((Class<V>) clazz, pageQuery, entity, false);

		List<V> entityVOList = TreeUtil.build(voPageableResult.getData().getRows(), root);

		return  voPageableResult.setDataRows(entityVOList);
	}

	public <E> List<E> findAllWithCond(Class<E> entity, Map<String, Object> condition) {

		String tableName = this.getTableNameByEntity(entity);
		StringBuilder sql = new StringBuilder(50).append("select a.* from ").append(tableName).append(" a ");
		List<Object> params = new ArrayList<>();

		if (!ObjectUtil.isBlank(condition)) {
			StringBuilder finalSql1 = new StringBuilder("where 1=1 ");
			String and = "and ";
			String aliases = "a.";
			condition.forEach((key, value) -> {
				Field field = null;
				try {
					field = ReflectionUtils.findRequiredField(entity, key);
				} catch (Exception e) {}
				if (field == null)
					return;
				if (field.getType().equals(String.class)) {
					finalSql1.append(and).append(" instr(").append(aliases).append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) {
					finalSql1.append(and).append(" DATE_FORMAT(").append(aliases).append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
				}
				else {
					finalSql1.append(and).append(aliases).append(NameConvert.humpToUnderLine(key)).append("= ? ");
				}
				params.add(value);
			});
			sql.append(finalSql1);
		}

		return this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(entity), params.toArray());
	}

	public <V, E> PageableResult<V> execQueryForPage(Class<V> clazz, PageQuery pageQuery, Class<E> entity, boolean isPage) {
		List<V> list;
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();
		String tableName = this.getTableNameByEntity(entity);

		StringBuilder sql = this.querySqlByTable(tableName, entity, params, condition, filter);

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		this.orderSql(sort, sql);

		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		if (isPage) {
			int totalPageNum = (total - 1) / pageSize + 1;
			currentPage = Math.min(currentPage, totalPageNum);
			list = this.execQueryForPage(clazz, sql.toString(), currentPage, pageSize, params.toArray());
		} else {
			list = this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(clazz), params.toArray());
		}

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	public <P, C, V> List<V> execQueryForList(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, String ...pTableAndCTableAndPIdKey) {
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();

		StringBuilder sql = this.querySqlByTable(pTableAndCTableAndPIdKey[0], pClass, params, condition, filter)
				.append(this.existsSql(pTableAndCTableAndPIdKey[1], "b", pTableAndCTableAndPIdKey[2], cClass, params, condition, filter)); // 约定：子表别名为b

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		this.orderSql(sort, sql);

		return this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(vo), params.toArray());
	}

	public <P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String ...pTableAndCTableAndPIdKey) {
		List<V> list;
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();

		StringBuilder sql = this.querySqlByTable(pTableAndCTableAndPIdKey[0], pClass, params, condition, filter)
				.append(this.existsSql(pTableAndCTableAndPIdKey[1], "b", pTableAndCTableAndPIdKey[2], cClass, params, condition, filter)); // 约定：子表别名为b

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		this.orderSql(sort, sql);

		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		if (isPage) {
			int totalPageNum = (total - 1) / pageSize + 1;
			currentPage = Math.min(currentPage, totalPageNum);
			list = this.execQueryForPage(vo, sql.toString(), currentPage, pageSize, params.toArray());
		} else {
			list = this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(vo), params.toArray());
		}

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String pTable, String cTable, String pIdKey) {
		return this.execQueryForPage(pClass, pClass, cClass, pageQuery, isPage, pTable, cTable, pIdKey);
	}

	public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String pIdKey) {
		String pTable = this.getTableNameByEntity(pClass);
		String cTable = this.getTableNameByEntity(cClass);

		return this.execQueryForPage(pClass, cClass, pageQuery, isPage, pTable, cTable, pIdKey);
	}
	
	private static final String UPDATE = "update ";
	private static final String WHERE = " where ";

	public <E> void dynamicUpdateByEntity(E entity) {
		Map<String, Object> params = ObjectUtil.toMap(entity);

		Class<?> clazz = this.clazz(entity);

		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		
		StringBuilder sql = new StringBuilder(UPDATE).append(tableName).append(" set ");

		String pKeyName = "";
		Object pKeyValue = null;

		Field[] fields = clazz.getDeclaredFields();
		List<Object> newParam = new ArrayList<>();

		List<Field> fieldList = params.entrySet().parallelStream().filter(p -> p.getValue() != null).map(item -> {
			String key = item.getKey();
			for (Field f : fields)
				if (key.equals(f.getName())) {
					return f;
				}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		for (Field f : fieldList) {

			String key = f.getName();
			Object value = params.get(key);

			Column propRelColumn = f.getAnnotation(Column.class);

			if (f.getAnnotation(Id.class) == null) {

				String columnName = propRelColumn == null ? NameConvert.humpToUnderLine(key) : propRelColumn.value();
				sql.append(columnName).append("=?,");

				newParam.add(value);

				continue;
			}

			pKeyName = propRelColumn != null ? propRelColumn.value() : NameConvert.humpToUnderLine(key);
			pKeyValue = value;
		}

		sql.delete(sql.length() - 1, sql.length());
		sql.append(WHERE).append(pKeyName).append(" = ? ");
		newParam.add(pKeyValue);
		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql.toString(), newParam.toArray());
	}

	public <E> void dynamicInsertByEntity(E entity) {
		Map<String, Object> params = ObjectUtil.toMap(entity);

		Class<?> clazz = this.clazz(entity);

		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuilder sql = new StringBuilder("insert into ")
				.append(tableName)
				.append("(");

		StringBuilder wildCard = new StringBuilder();

		Field[] fields = clazz.getDeclaredFields();
		List<Object> newParam = new ArrayList<>();

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = ObjectUtil.string(entry.getKey());
			Object value = entry.getValue();
			if (value == null)
				continue;

			for (Field f : fields) {
				if (key.equals(f.getName())) {

					Column propRelColumn = f.getAnnotation(Column.class);
					if (propRelColumn != null) {
						String columnName = propRelColumn.value();
						sql.append(NameConvert.humpToUnderLine(columnName)).append(",");
					}
					else {
						sql.append(NameConvert.humpToUnderLine(key)).append(",");

					}
					newParam.add(value);
					wildCard.append("?,");
				}
			}
		}

		sql.delete(sql.length() - 1, sql.length());
		wildCard.delete(wildCard.length() - 1, wildCard.length());
		sql.append(") values(").append(wildCard).append(")");

		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql.toString(), newParam.toArray());
	}

	public <E> void dynamicBatchUpdateByEntities(List<E> entities) {
		List<Map<String, Object>> params = entities.parallelStream().map(ObjectUtil::toMap).collect(Collectors.toList());

		if (params.isEmpty()) {
			return;
		}

		Class<?> clazz = this.clazz(entities.get(0));

		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuilder sql = new StringBuilder(UPDATE).append(tableName).append(" set ");

		String pKeyName = "";
		String pKeyField = null;

		Field[] fields = clazz.getDeclaredFields();
		List<String> wildCardKeys = new ArrayList<>();

		List<Field> fieldList = params.get(0).entrySet().parallelStream().filter(p -> p.getValue() != null).map(item -> {
			String key = item.getKey();
			for (Field f : fields) {
				if (key.equals(f.getName())) {
					return f;
				}
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		for (Field f : fieldList) {

			String key = f.getName();

			Column propRelColumn = f.getAnnotation(Column.class);
			String columnName = propRelColumn == null ? NameConvert.humpToUnderLine(key) : propRelColumn.value();

			if (f.getAnnotation(Id.class) == null) {

				sql.append(columnName).append("=?,");

				wildCardKeys.add(key);

				continue;
			}

			pKeyName = columnName;
			pKeyField = key;
		}

		sql.delete(sql.length() - 1, sql.length());
		sql.append(WHERE).append(pKeyName).append(" = ? ");
		wildCardKeys.add(pKeyField);

		List<Object[]> paramsList = params.parallelStream().map(entityMap -> {
			Object[] objects = new Object[entityMap.size()];
			for (int x = 0; x < objects.length; x++) {
				objects[x] = entityMap.get(wildCardKeys.get(x));
			}
			return objects;
		}).collect(Collectors.toList());

		this.getNamedParamJdbcTemplate().getJdbcOperations().batchUpdate(sql.toString(), paramsList);
	}

	public <E> void dynamicBatchInsertByEntities(List<E> entities) {
		List<Map<String, Object>> params = entities.parallelStream().map(ObjectUtil::toMap).collect(Collectors.toList());

		if (params.isEmpty())
			return;

		Class<?> clazz = this.clazz(entities.get(0));

		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");

		StringBuilder wildCard = new StringBuilder();

		Field[] fields = clazz.getDeclaredFields();
		List<String> wildCardKeys = new ArrayList<>();

		List<Field> fieldList = params.get(0).entrySet().parallelStream().filter(p -> p.getValue() != null).map(item -> {
			String key = item.getKey();
			Field field = null;
			for (Field f : fields) {
				if (key.equals(f.getName())) {
					field = f;
					break;
				}
			}
			return field;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		for (Field f : fieldList) {

			String key = f.getName();

			Column propRelColumn = f.getAnnotation(Column.class);

			if (propRelColumn != null) {
				String columnName = propRelColumn.value();
				sql.append(NameConvert.humpToUnderLine(columnName)).append(",");
			}
			else {
				sql.append(NameConvert.humpToUnderLine(key)).append(",");
			}
			wildCard.append("?,");
			wildCardKeys.add(key);
		}

		sql.delete(sql.length() - 1, sql.length());
		wildCard.delete(wildCard.length() - 1, wildCard.length());
		sql.append(") values(").append(wildCard).append(")");

		List<Object[]> paramsList = params.parallelStream().map(entityMap -> {
			Object[] objects = new Object[entityMap.size()];
			for (int x = 0; x < objects.length; x++) {
				objects[x] = entityMap.get(wildCardKeys.get(x));
			}
			return objects;
		}).collect(Collectors.toList());

		this.getNamedParamJdbcTemplate().getJdbcOperations().batchUpdate(sql.toString(), paramsList);
	}

	public <E> void deleteByIds(E entity, Object[] ids, boolean isLogic) {

		Class<?> clazz = this.clazz(entity);

		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}

		String pkName = this.getIdColNameByEntity(entity);

		StringBuilder sql = isLogic ? new StringBuilder(UPDATE)
				.append(tableName).append(" set delete_flag='deleted' ")
				: new StringBuilder("delete from ")
				.append(tableName);
		sql.append(WHERE).append(pkName).append(" in (?)");
		this.getNamedParamJdbcTemplate().getJdbcOperations().update(String.format(sql.toString().replace("?", "%s"), StringUtils.joinWith(",", ids)));
	}

	public <E> void deleteByMultiIds(E entity, Object[] ids, Object pid, boolean isLogic) {

		Class<?> clazz = this.clazz(entity);

		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}

		List<String> pkName = this.getMultiIdColNamesByEntity(entity.getClass());

		StringBuilder sql = isLogic ? new StringBuilder(UPDATE)
				.append(tableName).append(" set delete_flag='deleted' ")
				: new StringBuilder("delete from ")
				.append(tableName);
		sql.append(WHERE).append(pkName.get(0)).append(" in (?) and ");
		String sql0 = String.format(sql.toString().replace("?", "%s"), StringUtils.joinWith(",", ids)) + pkName.get(1) + " = ?";

		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql0, pid);
	}
}
