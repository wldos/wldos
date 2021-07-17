/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.repo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.NameConvert;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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

	/**
	 *针对领域模型实现的jdbc操作模板。与之相对的是经典jdbc模板jdbcTemplate。
	 * 支持实体级的CRUD。
	 */
	@Getter
	@Autowired
	private JdbcAggregateTemplate jdbcAggTemplate;

	/**
	 * 基于经典jdbc模板包装的命名参数jdbc操作模板。支持对象装配、驼峰和下划线的自动映射，如果实体bean名称或属性名称与数据库不是映射
	 * 关系，可以用{@code @Table @Column}等命名策略注解声明数据库物理定义的表名或列名。
	 * 针对复杂业务场景，构建领域关联的和动态参数交互的动态聚合查询。
	 */
	@Getter
	@Autowired
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	/**
	 * 自定义分页查询，带参数, 返回实体
	 *
	 * @param sql 查询sql
	 * @param params 针对sql排好序的查询参数
	 * @return 当前实体对象
	 */
	public <E> E queryForObject(Class<E> clazz, String sql, Object[] params) {

		return this.getNamedParamJdbcTemplate().getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<>(clazz), params);
	}

	/**
	 * 自定义分页查询，带参数, 返回实体
	 *
	 * @param sql 查询sql
	 * @param currentPage 当前页号
	 * @param pageSize 每页条数
	 * @param params 查询参数
	 * @return 结果集
	 */
	public List<Map<String, Object>> execQueryForPage(String sql, int currentPage, int pageSize, Object[] params) {

		int startRow = (currentPage - 1) * pageSize;
		StringBuilder mysql = new StringBuilder(200);
		mysql.append("select  A.* from ( ").append(sql).append(" ) A limit ").append(startRow).append(",").append(pageSize);

		return this.getNamedParamJdbcTemplate().getJdbcTemplate().queryForList(mysql.toString(), params);
	}

	/**
	 * 自定义分页查询，带参数, 返回实体
	 *
	 * @param clazz 实体bean或者领域bean
	 * @param sql 查询sql
	 * @param currentPage 当前页号
	 * @param pageSize 每页条数
	 * @param params 参数
	 * @return 结果集
	 */
	public <E> List<E> execQueryForPage(Class<E> clazz, String sql, int currentPage, int pageSize, Object ...params) {

		int startRow = (currentPage - 1) * pageSize;
		StringBuilder mysql = new StringBuilder(200);
		mysql.append("select  A.* from ( ").append(sql).append(" ) A limit ").append(startRow).append(",").append(pageSize);
		log.info(mysql.toString());
		return this.getNamedParamJdbcTemplate().getJdbcTemplate().query(mysql.toString(), new BeanPropertyRowMapper<>(clazz), params);
	}

	private String getTotalSql(String sql) {
		return "select count(1) as total from ( " + sql + " )";
	}

	/**
	 * 自定义分页查询，带参数和分页对象，返回通用分页模板结构
	 *
	 * @param sql 查询sql
	 * @param params 查询参数
	 * @return 一页数据
	 */
	public PageableResult<Map<String, Object>> execQueryForPageNoOrder(String sql, int currentPage, int pageSize, Object[] params) {

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<Map<String, Object>> list = this.execQueryForPage(sql, currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	/**
	 * 自定义分页查询，带参数和分页对象，返回对象级通用分页模板结构
	 *
	 * @param clazz 实体bean或领域bean
	 * @param sql 查询sql
	 * @param params sql对应的查询参数
	 * @return 一页数据
	 */
	public <E> PageableResult<E> execQueryForPageNoOrder(Class<E> clazz, String sql, int currentPage, int pageSize, Object[] params) {

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<E> list = this.execQueryForPage(clazz, sql, currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	/**
	 * 自定义分页查询支持排序，带参数和分页对象，返回通用分页模板结构
	 *
	 * @param sql 查询sql
	 * @param sqlOrder 排序
	 * @param params 查询参数
	 * @return 结果集
	 */
	public PageableResult<Map<String, Object>> execQueryForPage(String sql, String sqlOrder, int currentPage, int pageSize, Object[] params) {

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<Map<String, Object>> list = this.execQueryForPage(sql+ObjectUtil.string(sqlOrder), currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	/**
	 * 自定义分页查询支持排序，带参数和分页对象，返回对象级通用分页模板结构
	 *
	 * @param clazz 实体bean或领域bean
	 * @param sql 查询sql
	 * @param sqlOrder 排序sql
	 * @param pageQuery 分页查询
	 * @param params 查询参数
	 * @return 一页数据
	 */
	public <E> PageableResult<E> execQueryForPage(Class<E> clazz, String sql, String sqlOrder, PageQuery pageQuery, Object[] params) {

		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();

		List<Map<String, Object>> all = this.getNamedParamJdbcTemplate().getJdbcOperations().queryForList(this.getTotalSql(sql), params);
		int total = all.isEmpty() ? 0 : all.size();

		int totalPageNum = (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);

		List<E> list = this.execQueryForPage(clazz, sql+ObjectUtil.string(sqlOrder), currentPage, pageSize, params);

		return new PageableResult<>(total, currentPage, pageSize, list);
	}

	/**
	 * 根据实体bean获取实体对应的数据库表名
	 *
	 * @param clazz 实体bean的类型
	 * @param <E> 实体bean的类
	 * @return 表名
	 */
	public <E> String getTableNameByEntity(Class<E> clazz) {
		// 获取表名
		Table entityRelTable = clazz.getAnnotation(Table.class);
		String tableName = entityRelTable.value();
		if (ObjectUtil.isBlank(tableName)) {
			tableName = NameConvert.humpToUnderLine(clazz.getSimpleName()).toLowerCase();
		}

		return tableName;
	}

	/**
	 * 通过实体bean的实例获取数据库表主键字段名
	 *
	 * @param entity 实体bean的实例
	 * @param <E> 实体bean的类
	 * @return 实体对应的数据库表的主键名称
	 */
	public <E> String getIdColNameByEntity(E entity) {
		// 取出泛型T的Class
		Class<E> clazz = this.clazz(entity);

		return this.getIdColNameByEntity(clazz);
	}

	/**
	 * 通过实体bean的类型获取数据库表主键字段名，只针对一个主键的实体。
	 *
	 * @param clazz 实体bean的类型
	 * @param <E> 实体类
	 * @return 实体bean的主键名，只支持单主键的实体，建议关系表设计独立的主键不要用外键做组合主键
	 */
	public <E> String getIdColNameByEntity(Class<E> clazz) {
		// 获取字段
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
			}  // 判断是否主键
		}

		return pKeyName;
	}

	/**
	 * 通过实体bean的 类型 和 实例 获取数据库表组合主键字段名
	 *
	 * @param clazz 实体bean的类型
	 * @param <E> 具备组合主键的实体类
	 * @return 组合主键
	 */
	public <E> List<String> getMultiIdColNamesByEntity(Class<E> clazz) {
		// 获取字段
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


	/**
	 * 获取实体类运行时class
	 *
	 * @param entity 实体bean，需要实现默认构造
	 * @param <E> 实体类
	 * @return 实体类的类型
	 */
	public <E> Class<E> clazz(E entity) {
		// 取出泛型T的Class
		Class<E> clazz = null;
		try {
			clazz = (Class<E>) Class.forName(entity.getClass().getName());
		}
		catch (ClassNotFoundException e) {
			throw new JdbcToolsException("未找到类：", e);
		}
		return clazz;
	}
}
