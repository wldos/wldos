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
@Component
public class CommonJdbcOperation extends FreeJdbcTemplate {

	/**
	 * 标准实体的自定义分页查询，带参数和分页对象，返回带实体bean通用分页模板结构
	 *
	 * @param clazz 数据库表实体bean，不支持领域对象，领域对象请自定义sql移步上一方法
	 * @param pageQuery 分页参数
	 * @return 一页数据
	 */
	public <E> PageableResult<E> execQueryForPage(Class<E> clazz, PageQuery pageQuery) {

		return this.execQueryForPage(clazz, pageQuery, clazz, true);
	}

	/**
	 * 根据实体属性查询数据库组装vo为单元的树结构。
	 *
	 * @param clazz VO类型
	 * @param pageQuery 查询参数
	 * @param entity 实体bean E 的clazz
	 * @param root 根节点ID
	 * @param <V> 属性集是实体bean的副本或子集
	 * @return VO分页
	 */
	public <V extends TreeNode<V>, E> PageableResult<V> execQueryForTree(Class<?> clazz, PageQuery pageQuery, Class<E> entity, long root) {
		PageableResult<V> voPageableResult = this.execQueryForPage((Class<V>) clazz, pageQuery, entity, false);

		List<V> entityVOList = TreeUtil.build(voPageableResult.getData().getRows(), root);

		return  voPageableResult.setDataRows(entityVOList);
	}

	/**
	 * 带条件查询所有单体
	 *
	 * @param entity 实体bean
	 * @param condition 查询条件
	 * @param <E> 实体类型
	 * @return 实体list
	 */
	public <E> List<E> findAllWithCond(Class<E> entity, Map<String, Object> condition) {

		String tableName = this.getTableNameByEntity(entity);
		StringBuilder sql = new StringBuilder(50).append("select a.* from ").append(tableName).append(" a ");
		List<Object> params = new ArrayList<>();

		if (!ObjectUtil.isBlank(condition)) {
			StringBuilder finalSql1 = new StringBuilder("where 1=1 ");
			String and = "and";
			String aliases = "a.";
			condition.forEach((key, value) -> {
				Field field = ReflectionUtils.findRequiredField(entity, key);
				if (field.getType().equals(String.class)) {
					finalSql1.append(and).append(" instr(").append(aliases).append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
					finalSql1.append(and).append(" DATE_FORMAT(").append(aliases).append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
				}
				else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
					finalSql1.append(and).append(aliases).append(NameConvert.humpToUnderLine(key)).append("= ? ");
				}
				params.add(value);
			});
			sql.append(finalSql1);
		}

		return this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(entity), params.toArray());
	}



	/**
	 * 根据实体bean或实体vo、对应的表名称查询分页，支持查询条件、排序、过滤。
	 *
	 * @param clazz EntityVO的clazz
	 * @param pageQuery 分页参数
	 * @param entity 实体bean Entity的clazz
	 * @param <V> 必须和实体Entity保持一致的属性集，可以是子集
	 * @return VO分页
	 */
	public <V, E> PageableResult<V> execQueryForPage(Class<V> clazz, PageQuery pageQuery, Class<E> entity, boolean isPage) {
		List<V> list;
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();
		String tableName = this.getTableNameByEntity(entity);
		StringBuilder sql = new StringBuilder(50).append("select a.* from ").append(tableName).append(" a where 1=1 ");
		if (!ObjectUtil.isBlank(condition)) {
			StringBuilder finalSql1 = new StringBuilder();
			condition.forEach((key, value) -> {
				Field field = ReflectionUtils.findRequiredField(entity, key);
				if (field.getType().equals(String.class)) {
					finalSql1.append(" and instr(a.").append(NameConvert.humpToUnderLine(key)).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
					finalSql1.append(" and DATE_FORMAT(a.").append(NameConvert.humpToUnderLine(key)).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
				}
				else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
					finalSql1.append(" and a.").append(NameConvert.humpToUnderLine(key)).append("= ? ");
				}
				params.add(value);
			});
			sql.append(finalSql1);
		}

		if (filter != null && !filter.isEmpty()) {
			StringBuilder finalSql = new StringBuilder();
			filter.forEach((key, value) -> {
				finalSql.append(" and a.").append(NameConvert.humpToUnderLine(key)).append(" in (");
				value.forEach(item -> {
					finalSql.append("?,");
					params.add(item);
				});
			});

			sql.append(finalSql).delete(sql.length() - 1, sql.length());

			sql.append(")");
		}

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		if (!sort.isEmpty()) {
			StringBuilder temp = new StringBuilder(" order by ");
			sort.stream().iterator().forEachRemaining(s ->
				temp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "" : " desc "));
			sql.append(temp);
		}

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
	
	private static final String UPDATE = "update ";
	private static final String WHERE = " where ";

	/**
	 * 根据实体bean运行时动态拼装更新sql并更新
	 *
	 * @param entity 实体
	 */
	public <E> void dynamicUpdateByEntity(E entity) {
		Map<String, Object> params = ObjectUtil.toMap(entity);
		// 取出泛型T的Class
		Class<?> clazz = this.clazz(entity);

		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		
		StringBuilder sql = new StringBuilder(UPDATE).append(tableName).append(" set ");

		String pKeyName = "";
		Object pKeyValue = null;
		// 获取字段
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
			// 获取可能的@Column注解
			Column propRelColumn = f.getAnnotation(Column.class);

			if (f.getAnnotation(Id.class) == null) { // 不是主键按普通列处理

				String columnName = propRelColumn == null ? NameConvert.humpToUnderLine(key) : propRelColumn.value();
				sql.append(columnName).append("=?,");

				newParam.add(value);

				continue;
			}

			// 处理主键
			pKeyName = propRelColumn != null ? propRelColumn.value() : NameConvert.humpToUnderLine(key);
			pKeyValue = value;
		}

		sql.delete(sql.length() - 1, sql.length());
		sql.append(WHERE).append(pKeyName).append(" = ? ");
		newParam.add(pKeyValue);
		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql.toString(), newParam.toArray());
	}

	/**
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entity 实体
	 */
	public <E> void dynamicInsertByEntity(E entity) {
		Map<String, Object> params = ObjectUtil.toMap(entity);

		// 取出泛型T的Class
		Class<?> clazz = this.clazz(entity);
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuilder sql = new StringBuilder("insert into ")
				.append(tableName)
				.append("(");

		StringBuilder wildCard = new StringBuilder();

		// 获取字段
		Field[] fields = clazz.getDeclaredFields();
		List<Object> newParam = new ArrayList<>();

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = ObjectUtil.string(entry.getKey());
			Object value = entry.getValue();
			if (value == null)
				continue;

			for (Field f : fields) {
				if (key.equals(f.getName())) {

					// 获取普通属性的@Column注解
					Column propRelColumn = f.getAnnotation(Column.class);
					if (propRelColumn != null) {
						String columnName = propRelColumn.value();
						sql.append(NameConvert.humpToUnderLine(columnName)).append(",");
					}
					else { // 暂时不考虑标注在get方法上的注解
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

	/**
	 * 有选择地批量更新记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entities 实体集合
	 */
	public <E> void dynamicBatchUpdateByEntities(List<E> entities) {
		List<Map<String, Object>> params = entities.parallelStream().map(ObjectUtil::toMap).collect(Collectors.toList());

		if (params.isEmpty()) {
			return;
		}

		Class<?> clazz = this.clazz(entities.get(0));
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuilder sql = new StringBuilder(UPDATE).append(tableName).append(" set ");

		String pKeyName = "";
		String pKeyField = null;
		// 获取字段
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
			// 获取可能的@Column注解
			Column propRelColumn = f.getAnnotation(Column.class);
			String columnName = propRelColumn == null ? NameConvert.humpToUnderLine(key) : propRelColumn.value();

			if (f.getAnnotation(Id.class) == null) { // 不是主键按普通列处理

				sql.append(columnName).append("=?,");

				wildCardKeys.add(key);

				continue;
			}

			// 处理主键
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

	/**
	 * 有选择地批量insert记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entities 实体结合
	 */
	public <E> void dynamicBatchInsertByEntities(List<E> entities) {
		List<Map<String, Object>> params = entities.parallelStream().map(ObjectUtil::toMap).collect(Collectors.toList());

		if (params.isEmpty())
			return;

		Class<?> clazz = this.clazz(entities.get(0));
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");

		StringBuilder wildCard = new StringBuilder();

		// 获取字段
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
			// 获取可能的@Column注解
			Column propRelColumn = f.getAnnotation(Column.class);

			if (propRelColumn != null) {
				String columnName = propRelColumn.value();
				sql.append(NameConvert.humpToUnderLine(columnName)).append(",");
			}
			else { // 暂时不考虑标注在get方法上的注解
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

	/**
	 * 批量删除
	 *
	 * @param entity 实体bean的实例
	 * @param ids 主键
	 * @param isLogic 是否逻辑删，原则上使用逻辑删
	 */
	public <E> void deleteByIds(E entity, Object[] ids, boolean isLogic) {
		// 取出泛型T的Class
		Class<?> clazz = this.clazz(entity);
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		// 取出主键字段名，兼容非标设计，建议主键名为id
		String pkName = this.getIdColNameByEntity(entity);

		StringBuilder sql = isLogic ? new StringBuilder(UPDATE)
				.append(tableName).append(" set delete_flag='deleted' ")
				: new StringBuilder("delete from ")
				.append(tableName);
		sql.append(WHERE).append(pkName).append(" in (?)");
		this.getNamedParamJdbcTemplate().getJdbcOperations().update(String.format(sql.toString().replace("?", "%s"), StringUtils.joinWith(",", ids)));
	}

	/**
	 * 根据双组合主键批量删除
	 *
	 * @param entity 实体bean的实例
	 * @param ids 次因素主键
	 * @param pid 主因素主键
	 * @param isLogic 是否逻辑删，原则上使用逻辑删
	 */
	public <E> void deleteByMultiIds(E entity, Object[] ids, Object pid, boolean isLogic) {
		// 取出泛型T的Class
		Class<?> clazz = this.clazz(entity);
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		// 取出主键字段名，兼容非标设计，建议主键名为id
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
