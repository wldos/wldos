/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.base.tools;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wldos.common.dto.SQLTable;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.vo.TreeNode;

/**
 * 公共jdbc操作和业务辅助。
 *
 * @author 树悉猿
 * @date 2021/5/5
 * @version 1.0
 */
public interface CommonOperation extends FreeJdbcTemplate {

	/**
	 * 自定义基础sql分页查询，查询条件、过滤条件、排序动态生成。
	 *
	 * @param vo 结果集映射bean
	 * @param sqlNoWhere 结果集为VO的复杂查询sql，子查询存在where时主sql最外层必须带where
	 * @param pageQuery 分页查询参数
	 * @param sqlTables 用于声明sqlNoWhere中的表对应的bean类型和表别名，用于动态拼装查询条件，bean是能覆盖相关表查询条件的实体bean或者视图bean
	 * @param <V> VO类,系父表子集
	 * @return VO分页列表
	 */
	<V> PageableResult<V> execQueryForPage(Class<V> vo, String sqlNoWhere, PageQuery pageQuery, SQLTable... sqlTables);

	/**
	 * 自定义基础sql全量查询，查询条件、过滤条件、排序动态生成。
	 *
	 * @param vo 结果集映射bean
	 * @param sqlNoWhere 结果集为VO的复杂查询sql，子查询存在where时主sql最外层必须带where
	 * @param pageQuery 分页查询参数
	 * @param sqlTables 用于声明sqlNoWhere中的表对应的bean类型和表别名，用于动态拼装查询条件，bean是能覆盖相关表查询条件的实体bean或者视图bean
	 * @param <V> VO类,系父表子集
	 * @return VO列表
	 */
	<V> List<V> execQueryForList(Class<V> vo, String sqlNoWhere, PageQuery pageQuery, SQLTable... sqlTables);

	/**
	 * 标准实体的自定义分页查询，带参数和分页对象，返回带实体bean通用分页模板结构
	 *
	 * @param clazz 数据库表实体bean，不支持领域对象，领域对象请自定义sql移步上一方法
	 * @param pageQuery 分页参数
	 * @return 一页数据
	 */
	<E> PageableResult<E> execQueryForPage(Class<E> clazz, PageQuery pageQuery);

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
	<V extends TreeNode<V>, E> PageableResult<V> execQueryForTree(Class<?> clazz, PageQuery pageQuery, Class<E> entity, long root);

	/**
	 * 带条件查询所有单体
	 *
	 * @param entity 实体bean
	 * @param condition 查询条件
	 * @param <E> 实体类型
	 * @return 实体list
	 */
	<E> List<E> findAllWithCond(Class<E> entity, Map<String, Object> condition);

	/**
	 * 根据实体bean或实体vo、对应的表名称查询分页，支持查询条件、排序、过滤。
	 *
	 * @param clazz EntityVO的clazz
	 * @param pageQuery 分页参数
	 * @param entity 实体bean Entity的clazz
	 * @param <V> 必须和实体Entity保持一致的属性集，可以是子集
	 * @param isPage 是否分页，不分页将输出所有数据
	 * @return VO分页
	 */
	<V, E> PageableResult<V> execQueryForPage(Class<V> clazz, PageQuery pageQuery, Class<E> entity, boolean isPage);

	/**
	 * 根据父子关系表查询VO列表，支持父子表的查询条件、排序、过滤。
	 *
	 * @param vo VO实例
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pageQuery 分页查询参数
	 * @param pTableAndCTableAndPIdKey [pTable 父表名称, cTable 子表名称, pIdKey 父表在子表中存在的外键名]
	 * @param <P> 父表实体类
	 * @param <C> 子表实体类
	 * @param <V> VO类,系父表子集
	 * @return VO列表
	 */
	<P, C, V> List<V> execQueryForList(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, String... pTableAndCTableAndPIdKey);

	/**
	 * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
	 *
	 * @param vo VO实例
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pageQuery 分页查询参数
	 * @param isPage 是否分页，不分页将输出所有数据
	 * @param pTableAndCTableAndPIdKey [pTable 父表名称, cTable 子表名称, pIdKey 父表在子表中存在的外键名]
	 * @param <P> 父表实体类
	 * @param <C> 子表实体类
	 * @param <V> VO类,系父表子集
	 * @return 分页数据
	 */
	<P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String... pTableAndCTableAndPIdKey);

	/**
	 * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
	 *
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pageQuery 分页查询参数
	 * @param isPage 是否分页，不分页将输出所有数据
	 * @param pTable 父表名称
	 * @param cTable 子表名称
	 * @param pIdKey 父表在子表的外键名
	 * @param <PA> 父表实体类
	 * @param <C> 子表实体类
	 * @return 分页数据
	 */
	<PA, C> PageableResult<PA> execQueryForPage(Class<PA> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String pTable, String cTable, String pIdKey);

	/**
	 * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
	 *
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pageQuery 分页查询参数
	 * @param isPage 是否分页，不分页将输出所有数据
	 * @param pIdKey 父表在子表的外键名
	 * @param <P> 父表实体类
	 * @param <C> 子表实体类
	 * @return 分页数据
	 */
	<P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String pIdKey);

	/**
	 * 根据实体bean运行时动态拼装更新sql并更新
	 *
	 * @param entity 实体
	 */
	<E> void dynamicUpdateByEntity(E entity);

	/**
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entity 实体
	 */
	<E> void dynamicInsertByEntity(E entity);

	/**
	 * 有选择地批量更新记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entities 实体集合
	 */
	<E> void dynamicBatchUpdateByEntities(List<E> entities);

	/**
	 * 有选择地批量insert记录，空值不插入(采用数据库可能存在的默认值)。
	 *
	 * @param entities 实体结合
	 */
	<E> void dynamicBatchInsertByEntities(List<E> entities);

	/**
	 * 批量删除
	 *
	 * @param entity 实体bean的实例
	 * @param ids 主键
	 * @param isLogic 是否逻辑删，原则上使用逻辑删
	 */
	<E> void deleteByIds(E entity, Object[] ids, boolean isLogic);

	/**
	 * 根据双组合主键批量删除
	 *
	 * @param entity 实体bean的实例
	 * @param ids 次因素主键
	 * @param pid 主因素主键
	 * @param isLogic 是否逻辑删，原则上使用逻辑删
	 */
	<E> void deleteByMultiIds(E entity, Object[] ids, Object pid, boolean isLogic);

	/**
	 * 获取超级管理员
	 */
	List<Long> listSuperAdmin();

	/**
	 * 获取可信用户
	 */
	List<Long> listTrustMan();

	/**
	 * 安全起见，实时查询当前用户是否超级管理员
	 *
	 * @param userId 用户id
	 * @return 是否管理员
	 */
	boolean isAdmin(Long userId);

	/**
	 * 实时查询当前用户是否可信者
	 *
	 * @param userId 用户id
	 * @return 是否可信者
	 */
	boolean isCanTrust(Long userId);


	Long getCurUserId(HttpServletRequest request);

	String getToken(HttpServletRequest request);

	Long getTenantId(HttpServletRequest request);

	String getDomain(HttpServletRequest request);

	Long getDomainId(HttpServletRequest request);

	void applyDomainFilter(PageQuery pageQuery, HttpServletRequest request);

	void applyTenantFilter(PageQuery pageQuery, HttpServletRequest request);

	long getTokenExpTime(HttpServletRequest request);

	/**
	 * 生成状态码的逻辑涉及安全封装实现，二次开发可以自行实现
	 *
	 * @return 6位随机状态码+uuid
	 */
	String genStateCode();
}