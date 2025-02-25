/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.service;

import java.util.List;

import com.wldos.framework.support.internal.Base;
import com.wldos.framework.common.CommonOperation;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.dto.SQLTable;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.ResultJson;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

/**
 * 非实体基类service，对多个实体综合处理的业务类，可以注入多个不同实体的数据层访问接口。
 */
@Slf4j
@SuppressWarnings({ "unused" })
public abstract class NonEntityService extends Base {

	/**
	 * 扩展的jdbc和业务操作
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
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public <O> void insertOtherEntitySelective(O entity, boolean isAutoFill) {
		this.commonOperate.dynamicInsertByEntity(entity, isAutoFill);
	}

	/**
	 * 批量有选择地insert辅助实体bean记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entities 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public <O> void insertOtherEntitySelective(Iterable<O> entities, boolean isAutoFill) {
		this.commonOperate.dynamicBatchInsertByEntities((List<O>) entities, isAutoFill);
	}

	/**
	 * 根据实体属性更新，属性为空值的Long类型不更新。
	 *
	 * @param entity 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public <O> void updateOtherEntity(O entity, boolean isAutoFill) {
		this.commonOperate.dynamicUpdateByEntity(entity, isAutoFill);
	}

	/* ************************************** 分页查询API start************************************************/

	/**
	 * 多表关联动态查询分页，动态绑定查询条件，用于自定义关联查询场景的复杂sql生成
	 * 注意：约定sql中用到的所有表都存在标准驼峰格式名的实体bean，实体bean名必须是表名的驼峰格式化，也可以用覆盖所有查询条件的VO代替。
	 * 支持查询条件类型：字符串（模糊匹配）、日期（单个）、其他类型（=），默认and连接，更多功能后续增加
	 *
	 * @param vo 结果集映射bean
	 * @param sqlNoWhere 任意自定义sql，可以是复杂查询sql。子查询存在where时，主sql最外层必须带where语句。
	 * @param pageQuery 分页查询参数，可以指定查询条件、过滤条件、排序字段
	 * @param sqlTables 用于声明sqlNoWhere中的表对应的bean类型和表别名，用于动态拼装查询条件，bean是能覆盖相关表查询条件的实体bean或者视图bean
	 * @param <V> VO类
	 * @return VO分页列表
	 */
	public <V> PageableResult<V> execQueryForPage(Class<V> vo, String sqlNoWhere, PageQuery pageQuery, SQLTable... sqlTables) {
		return this.commonOperate.execQueryForPage(vo, sqlNoWhere, pageQuery, sqlTables);
	}

	/**
	 * 多表关联动态全量查询，查询条件、过滤条件、排序动态生成。
	 * 支持查询条件类型：字符串（模糊匹配）、日期（单个）、其他类型（=），默认and连接，更多功能后续增加
	 *
	 * @param vo 结果集映射bean
	 * @param sqlNoWhere 任意自定义sql，可以是复杂查询sql。子查询存在where时，主sql最外层必须带where语句。
	 * @param pageQuery 分页查询参数，可以指定查询条件、过滤条件、排序字段
	 * @param sqlTables 用于声明sqlNoWhere中的表对应的bean类型和表别名，用于动态拼装查询条件，bean是能覆盖相关表查询条件的实体bean或者视图bean
	 * @param <V> VO类
	 * @return VO列表
	 */
	public <V> List<V> execQueryForList(Class<V> vo, String sqlNoWhere, PageQuery pageQuery, SQLTable... sqlTables) {
		return this.commonOperate.execQueryForList(vo, sqlNoWhere, pageQuery, sqlTables);
	}

	/**
	 * 父子表查询父表子集VO的分页，支持父子表的查询条件、排序、过滤。
	 * 父子表 又叫 主从表
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
	 * 父子表查询父表的分页，支持父子表的查询条件、排序、过滤。
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
	 * 父子表查询父表的分页，支持父子表的查询条件、排序、过滤。
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
	 * 父子表查询父表vo的分页或全量结果集，支持父子表的查询条件、排序、过滤。
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
	 * 父子表查询VO列表，支持父子表的查询条件、排序、过滤。
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

	/* ************************************** 分页查询API end ************************************************/

	/**
	 * 获取请求用户id
	 */
	protected Long getUserId() {
		return this.commonOperate.getUserId();
	}

	/**
	 * 获取请求用户token
	 */
	protected String getToken() {
		return this.commonOperate.getToken();
	}

	/**
	 * 获取当前用户IP
	 */
	protected String getUserIp() {
		return this.commonOperate.getUserIp();
	}

	/** 是否多租户模式 */
	public boolean isMultiTenancy() {
		return this.commonOperate.isMultiTenancy();
	}

	/**
	 * 获取用户主企业id(租户)
	 */
	protected Long getTenantId() {
		return this.commonOperate.getTenantId();
	}

	/**
	 * 获取平台根域名
	 */
	protected String getPlatDomain() {
		return this.commonOperate.getPlatDomain();
	}

	/** 是否多站模式 */
	public boolean isMultiDomain() {
		return this.commonOperate.isMultiDomain();
	}

	/**
	 * 获取用户当前访问的域名
	 */
	protected String getDomain() {
		return this.commonOperate.getDomain();
	}

	/**
	 * 获取用户当前访问域名的id
	 *
	 * @return 域id
	 */
	protected Long getDomainId() {
		return this.commonOperate.getDomainId();
	}

	/**
	 * 针对分页查询应用域隔离
	 *
	 * @param pageQuery 分页查询参数
	 */
	protected void applyDomainFilter(PageQuery pageQuery) {
		this.commonOperate.applyDomainFilter(pageQuery);
	}

	/**
	 * 实体表分页查询应用租户隔离
	 *
	 * @param pageQuery 分页查询
	 */
	protected void applyTenantFilter(PageQuery pageQuery) {
		this.commonOperate.applyTenantFilter(pageQuery);
	}

	/**
	 * token超期时间
	 *
	 * @return expireTime
	 */
	protected long getTokenExpTime() {
		return this.commonOperate.getTokenExpTime();
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

	/* ************************************** sql操作API start************************************************/

	/**
	 * 根据查询条件动态绑定自定义查询sql，用于自定义关联查询场景的复杂sql生成
	 * 注意：约定sql中用到的所有表都存在标准驼峰格式名的实体bean，实体bean名必须是表名的驼峰格式化，也可以用覆盖所有查询条件的VO代替。
	 * 支持查询条件类型：字符串（模糊匹配）、日期（单个）、其他类型（=），默认and连接，更多功能后续增加
	 *
	 * @param sqlNoWhere 任意自定义sql，可以是复杂查询sql，子查询存在where时,主sql最外层必须带where语句
	 * @param params 参数表寄存器
	 * @param pageQuery 查询条件、过滤条件
	 * @return 查询sql
	 */
	protected StringBuilder genDynamicSql(String sqlNoWhere, List<Object> params, PageQuery pageQuery, SQLTable... sqlTables) {
		return this.commonOperate.querySqlByTable(sqlNoWhere, sqlTables, params, pageQuery.getCondition(), pageQuery.getFilter());
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

	/* ************************************** sql操作API end************************************************/

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