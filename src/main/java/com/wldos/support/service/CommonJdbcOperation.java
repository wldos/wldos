/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * @Title CommonJdbcOperation
 * @Package com.wldos.support.service
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/5
 * @Version 1.0
 */
@Component
public class CommonJdbcOperation extends FreeJdbcTemplate {

	/**
	 * 标准实体的自定义分页查询，带参数和分页对象，返回带实体bean通用分页模板结构
	 *
	 * @param clazz 数据库表实体bean，不支持领域对象，领域对象请自定义sql移步上一方法
	 * @param pageQuery
	 * @return
	 */
	public <Entity> PageableResult<Entity> execQueryForPage(Class<Entity> clazz, PageQuery pageQuery) {

		return this.execQueryForPage(clazz, pageQuery, clazz, true);
	}

	/**
	 * 根据实体属性查询数据库组装vo为单元的树结构。
	 *
	 * @param clazz
	 * @param pageQuery
	 * @param entity 实体bean Entity 的clazz
	 * @param root 根节点ID
	 * @param <EntityVO> 属性集是实体bean的副本或子集
	 * @return
	 */
	public <EntityVO extends TreeNode, Entity> PageableResult<EntityVO> execQueryForTree(Class<?> clazz, PageQuery pageQuery, Class<Entity> entity, long root) {
		PageableResult<EntityVO> voPageableResult = this.execQueryForPage((Class<EntityVO>) clazz, pageQuery, entity, false);

		List<EntityVO> entityVOList = TreeUtil.build(voPageableResult.getData().getRows(), root);

		return  voPageableResult.setDataRows(entityVOList);
	}

	/**
	 * 带条件查询所有单体
	 *
	 * @param entity
	 * @param condition
	 * @param <Entity>
	 * @return
	 */
	public <Entity> List<Entity> findAllWithCond(Class<Entity> entity, Map<String, Object> condition) {

		String tableName = this.getTableNameByEntity(entity);
		StringBuffer sql = new StringBuffer(50).append("select a.* from ").append(tableName).append(" a ");
		List<Object> params = new ArrayList<>();

		if (!ObjectUtil.isBlank(condition)) {
			StringBuffer finalSql1 = new StringBuffer("where 1=1 ");
			condition.entrySet().stream().forEach(entry -> {
				Field field = ReflectionUtils.findRequiredField(entity, entry.getKey());
				if (field.getType().equals(String.class)) {
					finalSql1.append(" and instr(a.").append(NameConvert.humpToUnderLine(entry.getKey())).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
					finalSql1.append(" and DATE_FORMAT(a.").append(NameConvert.humpToUnderLine(entry.getKey())).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
				}
				else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
					finalSql1.append(" and a.").append(NameConvert.humpToUnderLine(entry.getKey())).append("= ? ");
				}
				params.add(entry.getValue());
			});
			sql.append(finalSql1);
		}

		List<Entity> list = this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(entity), params.toArray());

		return list == null ? new ArrayList<>() : list;
	}



	/**
	 * 根据实体bean或实体vo、对应的表名称查询分页，支持查询条件、排序、过滤。
	 *
	 * @param clazz EntityVO的clazz
	 * @param pageQuery
	 * @param entity 实体bean Entity 的clazz
	 * @param <EntityVO> 必须和实体Entity保持一致的属性集，可以是子集
	 * @return
	 */
	public <EntityVO, Entity> PageableResult<EntityVO> execQueryForPage(Class<EntityVO> clazz, PageQuery pageQuery, Class<Entity> entity, boolean isPage) {
		List<EntityVO> list = new ArrayList<>();
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		List<Object> params = new ArrayList<>();
		String tableName = this.getTableNameByEntity(entity);
		StringBuffer sql = new StringBuffer(50).append("select a.* from ").append(tableName).append(" a where 1=1 ");
		if (!ObjectUtil.isBlank(condition)) {
			StringBuffer finalSql1 = new StringBuffer();
			condition.entrySet().stream().forEach(entry -> {
				Field field = ReflectionUtils.findRequiredField(entity, entry.getKey());
				if (field.getType().equals(String.class)) {
					finalSql1.append(" and instr(a.").append(NameConvert.humpToUnderLine(entry.getKey())).append(", ?) > 0 ");
				}
				else if (field.getType().equals(Timestamp.class)) { // 日期默认匹配当天
					finalSql1.append(" and DATE_FORMAT(a.").append(NameConvert.humpToUnderLine(entry.getKey())).append(", '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d') ");
				}
				else { // 包含所有的数字类型、日期类型。日期等的有界查询请移步前面自定义sql方法。
					finalSql1.append(" and a.").append(NameConvert.humpToUnderLine(entry.getKey())).append("= ? ");
				}
				params.add(entry.getValue());
			});
			sql.append(finalSql1);
		}

		if (filter != null && !filter.isEmpty()) {
			StringBuffer finalSql = new StringBuffer();
			filter.entrySet().stream().forEach(entry -> {
				finalSql.append(" and a.").append(NameConvert.humpToUnderLine(entry.getKey())).append(" in (");
				entry.getValue().stream().forEach(item -> {
					finalSql.append("?,");
					params.add(item);
				});
			});

			sql = sql.append(finalSql).delete(sql.length() - 1, sql.length());

			sql.append(")");
		}

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList("select count(1) as total from ( " + sql + " ) b", params.toArray());
		int total = all.isEmpty() ? 0 : Integer.parseInt(ObjectUtil.string(all.get(0).get("total")));

		if (!sort.isEmpty()) {
			StringBuffer temp = new StringBuffer(" order by ");
			sort.stream().iterator().forEachRemaining(s -> {
				temp.append(NameConvert.humpToUnderLine(s.getProperty())).append(" ").append(s.isAscending() ? "" : " desc ");
			});
			sql.append(temp);
		}

		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		if (isPage) {
			int totalPageNum = (total - 1) / pageSize + 1;
			currentPage = currentPage > totalPageNum ? totalPageNum : currentPage;
			list = this.execQueryForPage(clazz, sql.toString(), currentPage, pageSize, params.toArray());
		} else {
			list = this.getNamedParamJdbcTemplate().getJdbcOperations().query(sql.toString(), new BeanPropertyRowMapper<>(clazz), params.toArray());
		}

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	/**
	 * 根据实体bean运行时动态拼装更新sql并更新
	 *
	 * @param entity
	 */
	public <Entity> void dynamicUpdateByEntity(Entity entity) {
		Map<String, Object> params = ObjectUtil.toMap(entity);
		// 取出泛型T的Class
		Class<?> clazz = this.clazz(entity);

		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuffer sql = new StringBuffer("update ")
				.append(tableName)
				.append(" set ");

		String pKeyName = "";
		Object pKeyValue = null;
		// 获取字段
		Field[] fields = clazz.getDeclaredFields();
		List<Object> newParam = new ArrayList<>();

		for (Map.Entry entry : params.entrySet()) {
			String key = ObjectUtil.string(entry.getKey());
			Object value = entry.getValue();
			if (value == null)
				continue;

			for (Field f : fields) {
				if (key.equals(f.getName())) {
					if (ObjectUtil.isBlank(pKeyName)) { // 判断是否主键
						Id id = f.getAnnotation(Id.class);
						if (id != null) {
							Column propRelColumn = f.getAnnotation(Column.class);
							if (propRelColumn != null) {
								pKeyName = propRelColumn.value();
							}
							else {
								pKeyName = NameConvert.humpToUnderLine(key);
							}
							pKeyValue = value;
							continue;
						}
					}

					// 获取普通属性的@Column注解
					Column propRelColumn = f.getAnnotation(Column.class);
					if (propRelColumn != null) {
						String columnName = propRelColumn.value();
						sql.append(NameConvert.humpToUnderLine(columnName)).append("=?,");
						newParam.add(value);
					}
					else {
						String hkey = "";
						sql.append(hkey = NameConvert.humpToUnderLine(key)).append("=?,");

						newParam.add(value);
					}
				}
			}
		}

		sql = sql.delete(sql.length() - 1, sql.length());
		sql.append(" where ").append(pKeyName).append(" = ? ");
		newParam.add(pKeyValue);
		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql.toString(), newParam.toArray());
	}

	/**
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entity
	 */
	public <Entity> void dynamicInsertByEntity(Entity entity) {
		Map<String, Object> params = ObjectUtil.toMap(entity);

		// 取出泛型T的Class
		Class<?> clazz = this.clazz(entity);
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuffer sql = new StringBuffer("insert into ")
				.append(tableName)
				.append("(");

		StringBuilder wildCard = new StringBuilder();

		// 获取字段
		Field[] fields = clazz.getDeclaredFields();
		List<Object> newParam = new ArrayList<>();

		for (Map.Entry entry : params.entrySet()) {
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
						newParam.add(value);
					}
					else { // 暂时不考虑标注在get方法上的注解
						String hkey = "";
						sql.append(hkey = NameConvert.humpToUnderLine(key)).append(",");

						newParam.add(value);
					}
					wildCard.append("?,");
				}
			}
		}

		sql = sql.delete(sql.length() - 1, sql.length());
		wildCard = wildCard.delete(wildCard.length() - 1, wildCard.length());
		sql.append(") values(").append(wildCard).append(")");

		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql.toString(), newParam.toArray());
	}

	/**
	 * 有选择地批量更新记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entities
	 */
	public <Entity> void dynamicBatchUpdateByEntities(List<Entity> entities) {
		List<Map<String, Object>> params = entities.parallelStream().map(entity -> {
			return ObjectUtil.toMap(entity);
		}).collect(Collectors.toList());

		if (params == null || params.isEmpty())
			return;

		Class<?> clazz = this.clazz(entities.get(0));
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuffer sql = new StringBuffer("update ")
				.append(tableName)
				.append(" set ");

		String pKeyName = "";
		String pKeyField = null;
		// 获取字段
		Field[] fields = clazz.getDeclaredFields();
		List<String> wildCardKeys = new ArrayList<>();

		for (Map.Entry entry : params.get(0).entrySet()) {
			String key = ObjectUtil.string(entry.getKey());
			Object value = entry.getValue();
			if (value == null)
				continue;

			for (Field f : fields) {
				if (key.equals(f.getName())) {
					if (ObjectUtil.isBlank(pKeyName)) { // 判断是否主键
						Id id = f.getAnnotation(Id.class);
						if (id != null) {
							Column propRelColumn = f.getAnnotation(Column.class);
							if (propRelColumn != null) {
								pKeyName = propRelColumn.value();
							}
							else {
								pKeyName = NameConvert.humpToUnderLine(key);
							}
							pKeyField = key;
							continue;
						}
					}

					// 获取普通属性的@Column注解
					Column propRelColumn = f.getAnnotation(Column.class);
					if (propRelColumn != null) {
						String columnName = propRelColumn.value();
						sql.append(NameConvert.humpToUnderLine(columnName)).append("=?,");
					}
					else {
						sql.append(NameConvert.humpToUnderLine(key)).append("=?,");
					}

					wildCardKeys.add(key);
				}
			}
		}

		sql = sql.delete(sql.length() - 1, sql.length());
		sql.append(" where ").append(pKeyName).append(" = ? ");
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
	 * @param entities
	 */
	public <Entity> void dynamicBatchInsertByEntities(List<Entity> entities) {
		List<Map<String, Object>> params = entities.parallelStream().map(entity -> {
			return ObjectUtil.toMap(entity);
		}).collect(Collectors.toList());

		if (params == null || params.isEmpty())
			return;

		Class<?> clazz = this.clazz(entities.get(0));
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}
		StringBuffer sql = new StringBuffer("insert into ")
				.append(tableName)
				.append("(");

		StringBuilder wildCard = new StringBuilder();

		// 获取字段
		Field[] fields = clazz.getDeclaredFields();
		List<String> wildCardKeys = new ArrayList<>();

		for (Map.Entry entry : params.get(0).entrySet()) {
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
					wildCard.append("?,");
					wildCardKeys.add(key);
				}
			}
		}

		sql = sql.delete(sql.length() - 1, sql.length());
		wildCard = wildCard.delete(wildCard.length() - 1, wildCard.length());
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
	public <Entity> void deleteByIds(Entity entity, Object[] ids, boolean isLogic) {
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

		StringBuffer sql = isLogic ? new StringBuffer("update ")
				.append(tableName).append(" set delete_flag='deleted' ")
				: new StringBuffer("delete from ")
				.append(tableName);
		sql.append(" where ").append(pkName).append(" in (?)");
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
	public <Entity> void deleteByMultiIds(Entity entity, Object[] ids, Object pid, boolean isLogic) {
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

		StringBuffer sql = isLogic ? new StringBuffer("update ")
				.append(tableName).append(" set delete_flag='deleted' ")
				: new StringBuffer("delete from ")
				.append(tableName);
		sql.append(" where ").append(pkName.get(0)).append(" in (?) and ");
		String sql0 = String.format(sql.toString().replace("?", "%s"), StringUtils.joinWith(",", ids)) + pkName.get(1) + " = ?";

		this.getNamedParamJdbcTemplate().getJdbcOperations().update(sql0, new Object[]{ pid });
	}
}
