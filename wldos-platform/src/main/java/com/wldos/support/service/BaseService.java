/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.support.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.cms.dto.LevelNode;
import com.wldos.support.Base;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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
 * @param <P> 实体主键类型
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseService<R extends PagingAndSortingRepository<E, P>, E, P> extends Base {

	@Autowired
	protected R entityRepo;

	@Autowired
	protected CommonJdbcOperation commonJdbc;

	public E findById(P id) {
		Optional<E> res = entityRepo.findById(id);
		return res.orElse(null);
	}

	public List<E> findAll() {
		return (List<E>) entityRepo.findAll();
	}

	public List<E> findAllWithCond(Class<E> clazz, Map<String, Object> condition) {

		return this.commonJdbc.findAllWithCond(clazz, condition);
	}

	public boolean existsById(P id) {
		return this.entityRepo.existsById(id);
	}

	public Class<E>  getEntityClass(int index){
		ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
		return (Class)type.getActualTypeArguments()[index];
	}

	public String getTableNameByEntity(Class<E> clazz) {
		return this.commonJdbc.getTableNameByEntity(clazz);
	}

	public void save(E entity) {
		entityRepo.save(entity);
	}

	public void saveAll(Iterable<E> entities) {
		this.entityRepo.saveAll(entities);
	}

	public void delete(E entity) {
		entityRepo.delete(entity);
	}

	public void deleteById(P id) {
		Object[] params = { id };
		this.deleteByIds(params);
	}

	public void deleteByIds(Object... ids) {

		for (Object o : ids) {
			E entity = this.findById((P) o);
			if (entity != null) {
				this.commonJdbc.deleteByIds(entity, ids, true);
				break;
			}
		}
	}

	public <A> void deleteByEntityAndIds(A entity, boolean isLogic, Object... ids) {

		this.commonJdbc.deleteByIds(entity, ids, isLogic);
	}

	public <A> void deleteByMultiIds(A entity, Object[] ids, Object pid, boolean isLogic) {

		this.commonJdbc.deleteByMultiIds(entity, ids, pid, isLogic);
	}

	public void insertSelective(E entity) {
		this.commonJdbc.dynamicInsertByEntity(entity);
	}

	public void insertSelectiveAll(Iterable<E> entities) {
		this.commonJdbc.dynamicBatchInsertByEntities((List<E>) entities);
	}

	public <O> void insertOtherEntitySelective(O entity) {
		this.commonJdbc.dynamicInsertByEntity(entity);
	}

	public void update(E entity) {
		this.commonJdbc.dynamicUpdateByEntity(entity);
	}

	public void updateAll(Iterable<E> entities) {
		this.commonJdbc.dynamicBatchUpdateByEntities((List<E>) entities);
	}

	public <O> void updateOtherEntity(O entity) {
		this.commonJdbc.dynamicUpdateByEntity(entity);
	}

	@Deprecated
	public Page<E> findAll(Pageable pageable) {
		return entityRepo.findAll(pageable);
	}

	public List<Map<String, Object>> execQueryForPage(String sql, int currentPage, int pageSize) {
		return this.commonJdbc.execQueryForPage(sql, currentPage, pageSize, new Object[] {});
	}

	public PageableResult<E> execQueryForPage(E entity, PageQuery pageable) {
		return (PageableResult<E>) this.commonJdbc.execQueryForPage(entity.getClass(), pageable);
	}

	public <V> PageableResult<V> execQueryForPage(V vo, E entity, PageQuery pageable) {
		return (PageableResult<V>) this.commonJdbc.execQueryForPage(vo.getClass(), pageable, entity.getClass(), true);
	}

	public <D> PageableResult<D> execQueryForPage(Class<D> clazz, String sql, String sqlOrder, PageQuery pageQuery, Object ...params) {
		return  this.commonJdbc.execQueryForPage(clazz, sql, sqlOrder, pageQuery, params);
	}

	public <P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, String pTable, String cTable, String pIdKey, PageQuery pageQuery) {
		return this.commonJdbc.execQueryForPage(vo, pClass, cClass, pageQuery, true, pTable, cTable, pIdKey);
	}

	public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, String pTable, String cTable, String pIdKey, PageQuery pageQuery) {
		return this.commonJdbc.execQueryForPage(pClass, cClass, pageQuery, true, pTable, cTable, pIdKey);
	}

	public <P, C> PageableResult<P> execQueryForPage(Class<P> pClass, Class<C> cClass, String pIdKey, PageQuery pageQuery) {
		return this.commonJdbc.execQueryForPage(pClass, cClass, pageQuery, true, pIdKey);
	}

	public <V> PageableResult<V> execQueryForTree(V vo, E entity, PageQuery pageable, long root) {
		return (PageableResult<V>) this.commonJdbc.execQueryForTree(vo.getClass(), pageable, entity.getClass(), root);
	}

	public <P, C, V> PageableResult<V> execQueryForPage(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, boolean isPage, String ...pTableAndCTableAndPIdKey) {
		return this.commonJdbc.execQueryForPage(vo, pClass, cClass, pageQuery, isPage, pTableAndCTableAndPIdKey);
	}

	public <P, C, V> List<V> execQueryForList(Class<V> vo, Class<P> pClass, Class<C> cClass, PageQuery pageQuery, String ...pTableAndCTableAndPIdKey) {
		return this.commonJdbc.execQueryForList(vo, pClass, cClass, pageQuery, pTableAndCTableAndPIdKey);
	}

	/**
	 * 安全起见，实时查询当前用户是否超级管理员
	 *
	 * @param userId 用户id
	 * @return 是否管理员
	 */
	public boolean isAdmin(Long userId) {

		String key = RedisKeyEnum.WLDOS_ADMIN.toString();
		String value = ObjectUtil.string(this.cache.get(key));
		List<Long> adminIds;
		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {
				String sql = "select u.user_id from wo_org_user u where exists (select 1 from wo_org g where g.id=u.org_id "
						+ "and g.arch_id=u.arch_id and g.com_id=u.com_id and g.delete_flag='normal' and g.is_valid='1' and g.com_id=? and g.org_code=?)";

				Object[] params = { Constants.TOP_COM_ID, Constants.AdminOrgCode };
				List<Map<String, Object>> res = this.commonJdbc.getNamedParamJdbcTemplate().getJdbcTemplate().queryForList(sql, params);

				adminIds = res.parallelStream().map(r -> Long.parseLong(ObjectUtil.string(r.get("user_id")))).collect(Collectors.toList());

				assert !adminIds.isEmpty();

				value = om.writeValueAsString(adminIds);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return adminIds.contains(userId);
			}

			adminIds = new ObjectMapper().readValue(value, new TypeReference<List<Long>>() {});
			return adminIds.contains(userId);
		}
		catch (JsonProcessingException e) {
			this.getLog().error("json解析异常={} {}", value, e.getMessage());
			return false;
		}
	}

	protected StringBuilder querySqlByTable(String tableName, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
		return this.commonJdbc.querySqlByTable(tableName, entity, params, condition, filter);
	}

	protected void orderSql(Sort sort, @NonNull StringBuilder querySql) {
		this.commonJdbc.orderSql(sort, querySql);
	}

	protected String existsSql(String cTable, String cAlias, String pIdKey, Class<?> entity, List<Object> params, Map<String, Object> condition, Map<String, List<Object>> filter) {
		return this.commonJdbc.existsSql(cTable, cAlias, pIdKey, entity, params, condition, filter);
	}

	public List<LevelNode> queryTreeByParentId(String table, Object pId) {
		return this.commonJdbc.queryTreeByParentId(table, pId);
	}

	public List<LevelNode> queryTreeByChildId(String table, Object cId) {
		return this.commonJdbc.queryTreeByChildId(table, cId);
	}
}
