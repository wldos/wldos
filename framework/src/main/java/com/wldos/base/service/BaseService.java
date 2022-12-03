/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.Base;
import com.wldos.common.Constants;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

/**
 * 公共顶层service，实现基础curd操作，固定service层与entityRepo层。
 *
 * @param <R> 实体仓库
 * @param <E> 实体Bean
 * @param <PK> 实体主键类型
 */
@Slf4j
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class BaseService<R extends PagingAndSortingRepository<E, PK>, E, PK> extends Base {
	/**
	 * 实体Repo
	 */
	@Autowired
	@SuppressWarnings({ "all" })
	protected R entityRepo;

	/**
	 * 通用的jdbc和业务操作
	 */
	@Autowired
	@Lazy
	@SuppressWarnings({ "all" })
	protected CommonOperation commonOperate;

	/**
	 * 通过Id查询
	 *
	 * @param id 主键
	 * @return 实体
	 */
	public E findById(PK id) {
		Optional<E> res = entityRepo.findById(id);
		return res.orElse(null);
	}

	/**
	 * 无条件获取所有对象
	 *
	 * @return 实体列表
	 */
	public List<E> findAll() {
		return (List<E>) entityRepo.findAll();
	}

	/**
	 * 带条件查询所有匹配实体
	 *
	 * @param condition 查询条件
	 * @return 实体列表
	 */
	public List<E> findAllWithCond(Class<E> clazz, Map<String, Object> condition) {

		return this.commonOperate.findAllWithCond(clazz, condition);
	}

	public boolean existsById(PK id) {
		return this.entityRepo.existsById(id);
	}

	/**
	 * 获取当前类的泛型
	 *
	 * @param index 序号，从0开始
	 * @return 类的泛型class
	 */
	public Class<E> getEntityClass(int index) {
		//返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		//返回表示此类型实际类型参数的 Type 对象的数组()，index从0开始
		return (Class) type.getActualTypeArguments()[index];
	}

	/**
	 * 获取表名
	 *
	 * @param clazz 实体类的class
	 * @return 实体bean对应的数据库表名
	 */
	public String getTableNameByEntity(Class<E> clazz) {
		return this.commonOperate.getTableNameByEntity(clazz);
	}

	/**
	 * 添加
	 *
	 * @param entity 实体bean
	 */
	public void save(E entity) {
		entityRepo.save(entity);
	}

	/**
	 * 批量保存
	 *
	 * @param entities 实体bean迭代器
	 */
	public void saveAll(Iterable<E> entities) {
		this.entityRepo.saveAll(entities);
	}

	/**
	 * 删除
	 *
	 * @param entity 实体bean
	 */
	public void delete(E entity) {
		entityRepo.delete(entity);
	}

	/**
	 * 根据Id删除,杜绝物理删
	 *
	 * @param id 主键id
	 */
	public void deleteById(PK id) {
		Object[] params = { id };
		this.deleteByIds(params);
	}

	/**
	 * 批量删除,杜绝物理删
	 *
	 * @param ids 主键
	 */
	public void deleteByIds(Object... ids) {

		for (Object o : ids) {
			E entity = this.findById((PK) o);
			if (entity != null) {
				this.commonOperate.deleteByIds(entity, ids, true);
				break;
			}
		}
	}

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
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entity 实体
	 */
	public void insertSelective(E entity) {
		this.commonOperate.dynamicInsertByEntity(entity);
	}

	/**
	 * 批量有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entities 多个实体
	 */
	public void insertSelectiveAll(Iterable<E> entities) {
		this.commonOperate.dynamicBatchInsertByEntities((List<E>) entities);
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
	 * @param entity 实体bean
	 */
	public void update(E entity) {
		this.commonOperate.dynamicUpdateByEntity(entity);
	}

	/**
	 * 批量更新
	 *
	 * @param entities 实体bean迭代器
	 */
	public void updateAll(Iterable<E> entities) {
		this.commonOperate.dynamicBatchUpdateByEntities((List<E>) entities);
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
	 * 分页查询，符合spring data jdbc domain聚合根规范
	 * @param pageable 分页参数
	 * @return Page<E>
	 */
	@Deprecated
	public Page<E> findAll(Pageable pageable) {
		return entityRepo.findAll(pageable);
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
	 * 自定义分页查询，带参数，返回标准分页结构
	 *
	 * @param entity 实体bean
	 * @param pageable 分页参数
	 * @return 实体分页
	 */
	public PageableResult<E> execQueryForPage(E entity, PageQuery pageable) {
		return (PageableResult<E>) this.commonOperate.execQueryForPage(entity.getClass(), pageable);
	}

	/**
	 * 基于VO自定义分页查询支持排序、过滤，带参数，返回标准分页结构
	 *
	 * @param vo VO bean，请保持与实体bean一致的属性集
	 * @param pageable 分页参数
	 * @return VO分页
	 */
	public <V> PageableResult<V> execQueryForPage(V vo, E entity, PageQuery pageable) {
		return (PageableResult<V>) this.commonOperate.execQueryForPage(vo.getClass(), pageable, entity.getClass(), true);
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
	 * 树形结构查询，带参数，返回标准分页结构
	 *
	 * @param vo 请保持与实体bean一致的属性集
	 * @param pageable 分页参数
	 * @param root 根节点ID
	 * @return 一页数据
	 */
	public <V> PageableResult<V> execQueryForTree(V vo, E entity, PageQuery pageable, long root) {
		return (PageableResult<V>) this.commonOperate.execQueryForTree(vo.getClass(), pageable, entity.getClass(), root);
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
		String key = RedisKeyEnum.WLDOS_ADMIN.toString();
		String value = ObjectUtils.string(this.cache.get(key));
		List<Long> adminIds;
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtils.isBlank(value)) {
				String sql = "select u.user_id from wo_org_user u where exists (select 1 from wo_org g where g.id=u.org_id "
						+ "and g.arch_id=u.arch_id and g.com_id=u.com_id and g.delete_flag='normal' and g.is_valid='1' and g.com_id=? and g.org_code=?)";

				Object[] params = { Constants.TOP_COM_ID, Constants.AdminOrgCode };
				List<Map<String, Object>> res = this.commonOperate.getJdbcTemplate().queryForList(sql, params);

				adminIds = res.parallelStream().map(r -> Long.parseLong(ObjectUtils.string(r.get("user_id")))).collect(Collectors.toList());

				assert !adminIds.isEmpty();

				value = om.writeValueAsString(adminIds);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return adminIds.contains(userId);
			}

			adminIds = om.readValue(value, new TypeReference<List<Long>>() {});
			return adminIds.contains(userId);
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return false;
		}
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