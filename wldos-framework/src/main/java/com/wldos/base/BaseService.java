/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base;

import java.util.List;
import java.util.Map;

import com.wldos.common.dto.LevelNode;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.ResultJson;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

/**
 * 声明公共顶层service。
 */
@Slf4j
@SuppressWarnings({ "unused" })
abstract class BaseService extends Base {

	/**
	 * 通用的jdbc和业务操作
	 */
	@Autowired
	@Lazy
	@SuppressWarnings({ "all" })
	protected CommonOperation commonOperate;

	@Autowired
	protected ResultJson resJson;

	/**
	 * 批量删除,支持物理删
	 *
	 * @param entity 任意实体bean
	 * @param ids 实体bean的多个id
	 * @param isLogic 是否逻辑删，false物理删
	 * @param <A> any entity
	 */
	public <A> void deleteByEntityAndIds(A entity, boolean isLogic, Object... ids) {

		this.commonOperate.deleteByIds(entity, ids, isLogic);
	}

	/**
	 * 按双组合主键批量删除,支持物理删
	 *
	 * @param entity 任意实体bean的实例
	 * @param ids 次因素主键
	 * @param pid 主因素主键
	 * @param isLogic 是否逻辑删，原则上使用逻辑删
	 */
	public <A> void deleteByMultiIds(A entity, Object[] ids, Object pid, boolean isLogic) {

		this.commonOperate.deleteByMultiIds(entity, ids, pid, isLogic);
	}

	/**
	 * 有选择地insert辅助实体bean记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entity 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 */
	public <O> void insertOtherEntitySelective(O entity) {
		this.commonOperate.dynamicInsertByEntity(entity);
	}

	/**
	 * 批量有选择地insert辅助实体bean记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entities 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 */
	public <O> void insertOtherEntitySelective(Iterable<O> entities) {
		this.commonOperate.dynamicBatchInsertByEntities((List<O>) entities);
	}

	/**
	 * 根据实体属性更新，属性为空值的Long类型不更新。
	 *
	 * @param entity 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 */
	public <O> void updateOtherEntity(O entity) {
		this.commonOperate.dynamicUpdateByEntity(entity);
	}

	/**
	 * 自定义分页查询，不带参数
	 *
	 * @param sql 执行的sql
	 * @param currentPage 当前页号
	 * @param pageSize 每页条数
	 * @return 一页数据
	 */
	public List<Map<String, Object>> execQueryForPage(String sql, int currentPage, int pageSize) {
		return this.commonOperate.execQueryForPage(sql, currentPage, pageSize, new Object[] {});
	}

	/**
	 * 自定义分页查询支持排序、过滤，带参数和分页对象，返回对象级通用分页模板结构
	 *
	 * @param clazz 实体bean或领域bean，属性应该与sql返回结果集一致
	 * @param sql 查询sql，可以自由组装任意复杂sql
	 * @param sqlOrder 排序sql，查询总数时不宜排序，所以分开设置
	 * @param pageQuery 分页查询参数
	 * @param params 查询参数，查询条件和过滤参数来自pageQuery
	 * @return 一页数据
	 */
	public <D> PageableResult<D> execQueryForPage(Class<D> clazz, String sql, String sqlOrder, PageQuery pageQuery, Object... params) {
		return this.commonOperate.execQueryForPage(clazz, sql, sqlOrder, pageQuery, params);
	}

	/**
	 * 根据父子关系表查询父表子集VO的分页，支持父子表的查询条件、排序、过滤。
	 *
	 * @param vo VO实例
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pTable 父表名称
	 * @param cTable 子表名称
	 * @param pIdKey 父表在子表中的外键名
	 * @param pageQuery 分页查询参数
	 * @param <P> 父表实体类
	 * @param <C> 子表实体类
	 * @param <V> VO类,系父表子集
	 * @return 分页数据
	 */
	public <P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, String pTable, String cTable, String pIdKey, PageQuery pageQuery) {
		return this.commonOperate.execQueryForPage(vo, pClass, cClass, pageQuery, true, pTable, cTable, pIdKey);
	}

	/**
	 * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
	 *
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pTable 父表名称
	 * @param cTable 子表名称
	 * @param pIdKey 父表在子表中的外键名
	 * @param pageQuery 分页查询参数
	 * @param <P> 父表实体类
	 * @param <C> 子表实体类
	 * @return 分页数据
	 */
	public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, String pTable, String cTable, String pIdKey, PageQuery pageQuery) {
		return this.commonOperate.execQueryForPage(pClass, cClass, pageQuery, true, pTable, cTable, pIdKey);
	}

	/**
	 * 根据父子关系表查询父表的分页，支持父子表的查询条件、排序、过滤。
	 *
	 * @param pClass 父表实体bean的类型
	 * @param cClass 子表实体bean的类型
	 * @param pIdKey 父表在子表中的外键名
	 * @param pageQuery 分页查询参数
	 * @param <P> 父表实体类
	 * @param <C> 子表实体类
	 * @return 分页数据
	 */
	public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, String pIdKey, PageQuery pageQuery) {
		return this.commonOperate.execQueryForPage(pClass, cClass, pageQuery, true, pIdKey);
	}

	/**
	 * 根据父子关系表查询父表vo的分页或不分页，支持父子表的查询条件、排序、过滤。
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
	public <P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String... pTableAndCTableAndPIdKey) {
		return this.commonOperate.execQueryForPage(vo, pClass, cClass, pageQuery, isPage, pTableAndCTableAndPIdKey);
	}

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
	public <P, C, V> List<V> execQueryForList(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, String... pTableAndCTableAndPIdKey) {
		return this.commonOperate.execQueryForList(vo, pClass, cClass, pageQuery, pTableAndCTableAndPIdKey);
	}

	/**
	 * 安全起见，实时查询当前用户是否超级管理员
	 *
	 * @param userId 用户id
	 * @return 是否管理员
	 */
	public boolean isAdmin(Long userId) {
		return this.commonOperate.isAdmin(userId);
	}

	/**
	 * 实时查询当前用户是否可信者
	 *
	 * @param userId 用户id
	 * @return 是否可信者
	 */
	public boolean isCanTrust(Long userId) {
		return this.commonOperate.isCanTrust(userId);
	}

	/**
	 * 基于单表指定参数获取查询sql，并绑定查询条件，用于单表、父子关联查询场景的复杂sql生成
	 * 注意：约定生成的表查询sql的表别名默认为a。
	 *
	 * @param tableName 表名
	 * @param entity 当前表对应的实体类型
	 * @param params 参数表寄存器
	 * @param condition 查询条件
	 * @param filter 过滤参数
	 * @return 查询sql
	 */
	protected StringBuilder querySqlByTable(String tableName, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
		return this.commonOperate.querySqlByTable(tableName, entity, params, condition, filter);
	}

	/**
	 * 根据排序参数填充查询sql
	 *
	 * @param sort 排序参数
	 * @param querySql 查询sql, 来自querySqlByTable
	 */
	protected void orderSql(Sort sort, @NonNull StringBuilder querySql) {
		this.commonOperate.orderSql(sort, querySql);
	}

	/**
	 * 根据参数生成判断子表是否存在记录的sql，用于父子表关联查询时根据子表的字段查询父表的情况
	 * 约定父子表的主键都是id，父表的别名为a，并且主键关联。
	 *
	 * @param cTable 子表名
	 * @param cAlias 子表别名
	 * @param pAlias 父表别名
	 * @param pIdKey 父表在子表中存在的外键名
	 * @param entity 子表实体bean类型
	 * @param params 参数表寄存器
	 * @return 存在查询sql
	 */
	protected String existsSql(String cTable, String cAlias, String pAlias, String pIdKey, Class<?> entity, List<Object> params, PageQuery pageQuery) {

		return this.commonOperate.existsSql(cAlias, entity, this.commonOperate.makeBaseExistsSql(cTable, cAlias, pAlias, pIdKey),
				params, pageQuery.getCondition(), pageQuery.getFilter());
	}

	/**
	 * 通过父节点id查询所有子节点(含父节点自身)
	 *
	 * @param table 带parent_id 和 id的目标表名称
	 * @param pId 要查询的父节点id
	 * @return 一棵节点树
	 */
	public List<LevelNode> queryTreeByParentId(String table, Object pId) {
		return this.commonOperate.queryTreeByParentId(table, pId);
	}

	/**
	 * 通过子节点id查询所有父节点(含子节点自身)
	 *
	 * @param table 带parent_id 和 id的目标表名称
	 * @param cId 要查询的子节点id
	 * @return 一棵节点树
	 */
	public List<LevelNode> queryTreeByChildId(String table, Object cId) {
		return this.commonOperate.queryTreeByChildId(table, cId);
	}
}