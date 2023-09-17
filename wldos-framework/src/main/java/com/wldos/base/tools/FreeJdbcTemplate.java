/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.base.tools;

import java.util.List;
import java.util.Map;

import com.wldos.common.dto.LevelNode;
import com.wldos.common.dto.SQLTable;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.support.God;

import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;

/**
 * 实现自由的jdbc操作，自定义cache，支持实体级的CRUD 和 自定义复杂SQL，定义公共jdbc操作模板。
 * 主要提供基础API，用于构建动态API。
 *
 * @author 树悉猿
 * @date 2021-04-16
 * @version V1.0
 */
@SuppressWarnings("unused")
public interface FreeJdbcTemplate extends God {

	/**
	 *	针对领域模型实现的jdbc操作模板。与之相对的是经典jdbc模板jdbcTemplate。
	 * 支持实体级的CRUD。
	 */
	JdbcAggregateTemplate getJdbcAggTemplate();

	/**
	 * 基于经典jdbc模板包装的命名参数jdbc操作模板。支持对象装配、驼峰和下划线的自动映射，如果实体bean名称或属性名称与数据库不是映射
	 * 关系，可以用{@code @Table @Column}等命名策略注解声明数据库物理定义的表名或列名。
	 * 针对复杂业务场景，构建领域关联的和动态参数交互的动态聚合查询。
	 * 只要getJdbcOperations()能够用，就不要使用getJdbcTemplate()
	 */
	NamedParameterJdbcTemplate getNamedParamJdbcTemplate();

	JdbcTemplate getJdbcTemplate();

	JdbcOperations getJdbcOperations();

	BaseWrap getBaseWrap();

	/**
	 * 任意自定义查询sql，动态绑定查询条件，用于自定义关联查询场景的复杂sql生成
	 * 注意：约定sql中用到的所有表都存在标准驼峰格式名的实体bean，实体bean名必须是表名的驼峰格式化，也可以用覆盖所有查询条件的VO代替。
	 *
	 * @param sqlNoWhere 任意自定义sql，可以是复杂查询sql，子查询存在where时主sql最外层必须带where
	 * @param sqlTables 用于声明sqlNoWhere中的表对应的bean类型和表别名，用于动态拼装查询条件，bean是能覆盖相关表查询条件的实体bean或者视图bean
	 * @param params 参数表寄存器
	 * @param condition 查询条件
	 * @param filter 过滤参数
	 * @return 查询sql
	 */
	StringBuilder querySqlByTable(String sqlNoWhere, SQLTable[] sqlTables, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter);

	/**
	 * 自定义单行查询，指定查询条件
	 *
	 * @param sql 查询sql
	 * @param params 针对sql排好序的查询参数
	 * @return 当前实体对象
	 */
	<E> E queryForObject(Class<E> clazz, String sql, Object[] params);

	/**
	 * 自定义全量查询，指定查询条件
	 *
	 * @param clazz 任意类
	 * @param sql 要执行的sql
	 * @param params 参数数组
	 * @param <C> 任意类的类型
	 * @return 结果集
	 */
	<C> List<C> queryForList(Class<C> clazz, String sql, Object[] params);

	/* *******************************************分页基础API start****************************************************/

	/**
	 * 自定义分页查询，指定查询条件
	 *
	 * @param sql 查询sql
	 * @param currentPage 当前页号
	 * @param pageSize 每页条数
	 * @param params 查询参数
	 * @return 结果集
	 */
	List<Map<String, Object>> execQueryForPage(String sql, int currentPage, int pageSize, Object[] params);

	/**
	 * 自定义分页查询，指定查询条件
	 *
	 * @param clazz 实体bean或者领域bean
	 * @param sql 查询sql
	 * @param currentPage 当前页号
	 * @param pageSize 每页条数
	 * @param params 可变参数
	 * @return 结果集
	 */
	<E> List<E> execQueryForPage(Class<E> clazz, String sql, int currentPage, int pageSize, Object... params);

	/**
	 * 自定义分页模板查询，指定查询条件
	 *
	 * @param sql 查询sql
	 * @param params 查询参数
	 * @return 一页数据
	 */
	PageableResult<Map<String, Object>> execQueryForPageNoOrder(String sql, int currentPage, int pageSize, Object... params);

	/**
	 * 自定义分页查询，带参数和分页对象，返回对象级通用分页模板结构
	 *
	 * @param clazz 实体bean或领域bean
	 * @param sql 查询sql
	 * @param params sql对应的查询参数
	 * @return 一页数据
	 */
	<E> PageableResult<E> execQueryForPageNoOrder(Class<E> clazz, String sql, int currentPage, int pageSize, Object... params);

	/**
	 * 自定义分页查询支持排序，带参数和分页对象，返回通用分页模板结构
	 *
	 * @param sql 查询sql
	 * @param sqlOrder 排序
	 * @param params 查询参数
	 * @return 结果集
	 */
	PageableResult<Map<String, Object>> execQueryForPage(String sql, String sqlOrder, int currentPage, int pageSize, Object[] params);

	/**
	 * 自定义分页查询，支持自定义排序，带参数和分页对象，返回对象级通用分页模板结构
	 *
	 * @param clazz 实体bean或领域bean
	 * @param sql 查询sql
	 * @param sqlOrder 排序sql
	 * @param pageQuery 分页查询
	 * @param params 查询参数
	 * @return 一页数据
	 */
	<E> PageableResult<E> execQueryForPage(Class<E> clazz, String sql, String sqlOrder, PageQuery pageQuery, Object... params);

	/* *******************************************分页基础API end ****************************************************/

	/**
	 * 根据实体bean获取实体对应的数据库表名
	 *
	 * @param clazz 实体bean的类型
	 * @param <E> 实体bean的类
	 * @return 表名
	 */
	<E> String getTableNameByEntity(Class<E> clazz);

	/**
	 * 通过实体bean的实例获取数据库表主键字段名
	 *
	 * @param entity 实体bean的实例
	 * @param <E> 实体bean的类
	 * @return 实体对应的数据库表的主键名称
	 */
	<E> String getIdColNameByEntity(E entity);

	/**
	 * 通过实体bean的类型获取数据库表主键字段名，只针对一个主键的实体。
	 *
	 * @param clazz 实体bean的类型
	 * @param <E> 实体类
	 * @return 实体bean的主键名，只支持单主键的实体，建议关系表设计独立的主键不要用外键做组合主键
	 */
	<E> String getIdColNameByEntity(Class<E> clazz);

	/**
	 * 通过实体bean的 类型 和 实例 获取数据库表组合主键字段名
	 *
	 * @param clazz 实体bean的类型
	 * @param <E> 具备组合主键的实体类
	 * @return 组合主键
	 */
	<E> List<String> getMultiIdColNamesByEntity(Class<E> clazz);

	/**
	 * 获取实体类运行时class
	 *
	 * @param entity 实体bean，需要实现默认构造
	 * @param <E> 实体类
	 * @return 实体类的类型
	 */
	<E> Class<E> clazz(E entity);

	/* *******************************************SQL构建基础API start****************************************************/

	/**
	 * 基于单表指定参数获取查询sql，并绑定查询条件，用于单表、父子关联查询场景的复杂sql生成
	 * 注意：约定生成的表查询sql的表别名默认为a。多表自定义动态查询，见#querySqlByTable重载方法
	 *
	 * @param tableName 表名
	 * @param entity 当前表对应的实体类型
	 * @param params 参数表寄存器
	 * @param condition 查询条件
	 * @param filter 过滤参数
	 * @return 查询sql
	 */
	StringBuilder querySqlByTable(String tableName, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter);

	/**
	 * 根据排序参数填充查询sql，当字段存在两个以上表存在时用别名（"a.foo" : "descend" 会解析成 order by a.foo）,支持特殊处理
	 * （如字段连接：“a.foo | b.bar” : "asc" 会解析成order by a.foo | b.bar）
	 *
	 * @param sort 排序参数
	 * @param querySql 查询sql, 来自querySqlByTable
	 */
	void orderSql(Sort sort, @NonNull StringBuilder querySql);

	/**
	 * 创建基础exists sql，用于基于父表的子表存在查询，默认父表主键名为id
	 *
	 * @param cTable 子表名
	 * @param cAlias 子表别名
	 * @param pAlias 父表别名
	 * @param pIdKey 父表主键在子表中的外键名
	 * @return 带最小父子关联条件的exists sql，形如：select 1 from cTable cAlias where cAlias.pIdKey=pAlias.id，方便追加其他关联字段
	 */
	String makeBaseExistsSql(String cTable, String cAlias, String pAlias, String pIdKey);

	/**
	 * 根据参数生成判断子表是否存在记录的sql，用于父子表关联查询时根据子表的字段查询父表的情况
	 * 约定父子表的主键都是id，父表的别名为a，并且主键关联。
	 *
	 * @param cAlias 子表别名
	 * @param entity 子表实体bean类型
	 * @param params 参数表寄存器
	 * @param condition 查询条件
	 * @param filter 过滤参数
	 * @return 存在查询sql
	 */
	String existsSql(String cAlias, Class<?> entity, String baseExistSql, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter);

	/**
	 * 根据参数生成判断子表是否存在记录的sql，用于父子表关联查询时根据子表的字段查询父表的情况
	 * 约定父子表的主键都是id，父表的别名为a，并且主键关联。
	 *
	 * @param cAlias 子表别名
	 * @param entity 子表实体bean类型
	 * @param params 参数表寄存器
	 * @param pageQuery 查询条件
	 * @return 存在查询sql
	 */
	String existsSql(String cAlias, Class<?> entity, String baseExistSql, List<Object> params, PageQuery pageQuery);

	/* *******************************************SQL构建基础API end****************************************************/

	/**
	 * 通过父节点id查询所有子节点(含父节点自身)
	 *
	 * @param table 带parent_id 和 id的目标表名称
	 * @param pId 要查询的父节点id
	 * @return 一棵节点树
	 */
	List<LevelNode> queryTreeByParentId(String table, Object pId);

	/**
	 * 通过子节点id查询所有父节点(含子节点自身)
	 *
	 * @param table 带parent_id 和 id的目标表名称
	 * @param cId 要查询的子节点id
	 * @return 一棵节点树
	 */
	List<LevelNode> queryTreeByChildId(String table, Object cId);

	/** 在线用户数 */
	int queryOnlineUserNum();

	/** 系统用户数 */
	int queryUserSum();

	/** 虚拟域数 */
	int queryDomainSum();

	/** 租户数 */
	int queryComSum();
}