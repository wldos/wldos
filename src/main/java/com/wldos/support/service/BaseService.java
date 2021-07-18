/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wldos.support.Base;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.constant.PubConstants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 公共顶层service，实现基础curd操作，固定service层与entityRepo层。
 *
 * @param <R> 实体仓库
 * @param <E> 实体Bean
 * @param <P> 实体主键类型
 */
@Slf4j
public class BaseService<R extends PagingAndSortingRepository<E, P>, E, P> extends Base {
	/**
	 * 实体Repo
	 */
	@Autowired
	protected R entityRepo;

	/**
	 * 自由的jdbc操作
	 */
	@Autowired
	protected CommonJdbcOperation commonJdbc;

	/**
	 * 通过Id查询
	 *
	 * @param id
	 * @return
	 */
	public E findById(P id) {
		Optional<E> res = entityRepo.findById(id);
		return res.isPresent() ? res.get() : null;
	}

	/**
	 * 无条件获取所有对象
	 *
	 * @return
	 */
	public List<E> findAll() {
		return (List<E>) entityRepo.findAll();
	}

	/**
	 * 带条件查询所有匹配实体
	 *
	 * @param condition
	 * @return
	 */
	public List<E> findAllWithCond(Class<E> clazz, Map<String, Object> condition) {

		return this.commonJdbc.findAllWithCond(clazz, condition);
	}

	/**
	 * 获取当前类的泛型
	 *
	 * @param index
	 * @return
	 */
	public Class<E>  getEntityClass(int index){
		//返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
		ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
		//返回表示此类型实际类型参数的 Type 对象的数组()，index从0开始
		return (Class)type.getActualTypeArguments()[index];
	}

	/**
	 * 获取表名
	 *
	 * @param clazz
	 * @return
	 */
	public String getTableNameByEntity(Class<E> clazz) {
		return this.commonJdbc.getTableNameByEntity(clazz);
	}

	/**
	 * 添加
	 *
	 * @param entity
	 */
	public void save(E entity) {
		entityRepo.save(entity);
	}

	/**
	 * 批量保存
	 *
	 * @param entities
	 */
	public void saveAll(Iterable<E> entities) {
		this.entityRepo.saveAll(entities);
	}

	/**
	 * 删除
	 *
	 * @param entity
	 */
	public void delete(E entity) {
		entityRepo.delete(entity);
	}

	/**
	 * 根据Id删除,杜绝物理删
	 *
	 * @param id
	 */
	public void deleteById(P id) {
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
			E entity = this.findById((P) o);
			if (entity != null) {
				this.commonJdbc.deleteByIds(entity, ids, true);
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
	public <A> void deleteByIds(A entity, Object[] ids, boolean isLogic) {

		this.commonJdbc.deleteByIds(entity, ids, isLogic);
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

		this.commonJdbc.deleteByMultiIds(entity, ids, pid, isLogic);
	}

	/**
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entity 实体
	 */
	public void insertSelective(E entity) {
		this.commonJdbc.dynamicInsertByEntity(entity);
	}

	/**
	 * 批量有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entities 多个实体
	 */
	public void insertSelectiveAll(Iterable<E> entities) {
		this.commonJdbc.dynamicBatchInsertByEntities((List<E>) entities);
	}

	/**
	 * 有选择地insert辅助实体bean记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entity 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 */
	public <O> void insertOtherEntitySelective(O entity) {
		this.commonJdbc.dynamicInsertByEntity(entity);
	}

	/**
	 * 根据实体属性更新，属性为空值的Long类型不更新。
	 *
	 * @param entity
	 */
	public void update(E entity) {
		this.commonJdbc.dynamicUpdateByEntity(entity);
	}

	/**
	 * 批量更新
	 *
	 * @param entities
	 */
	public void updateAll(Iterable<E> entities) {
		this.commonJdbc.dynamicBatchUpdateByEntities((List<E>) entities);
	}

	/**
	 * 根据实体属性更新，属性为空值的Long类型不更新。
	 *
	 * @param entity 辅助系实体
	 * @param <O> 其他实体，比如主表的子表对应的实体bean
	 */
	public <O> void updateOtherEntity(O entity) {
		this.commonJdbc.dynamicUpdateByEntity(entity);
	}

	/**
	 * 分页查询，符合spring data jdbc domain聚合根规范
	 * @param pageable 分页参数
	 * @return Page<E>
	 */
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
		return this.commonJdbc.execQueryForPage(sql, currentPage, pageSize, new Object[] {});
	}

	/**
	 * 自定义分页查询，带参数，返回标准分页结构
	 *
	 * @param entity 实体bean
	 * @param pageable 分页参数
	 * @return 实体分页
	 */
	public PageableResult<E> execQueryForPage(E entity, PageQuery pageable) {
		return (PageableResult<E>) this.commonJdbc.execQueryForPage(entity.getClass(), pageable);
	}

	/**
	 * 基于VO自定义分页查询，带参数，返回标准分页结构
	 *
	 * @param vo VO bean，请保持与实体bean一致的属性集
	 * @param pageable 分页参数
	 * @return VO分页
	 */
	public <V> PageableResult<V> execQueryForPage(V vo, E entity, PageQuery pageable) {
		return (PageableResult<V>) this.commonJdbc.execQueryForPage(vo.getClass(), pageable, entity.getClass(), true);
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
		return (PageableResult<V>) this.commonJdbc.execQueryForTree(vo.getClass(), pageable, entity.getClass(), root);
	}

	/**
	 * 安全起见，实时查询当前用户是否超级管理员
	 *
	 * @param userId 用户id
	 * @return 是否管理员
	 */
	public boolean isAdmin(Long userId) {
		String sql = "select 1 from wo_org_user u where u.user_id=? and EXISTS (SELECT 1 from wo_org g where g.id=u.org_id "
				+ "and g.arch_id=u.arch_id and g.com_id=u.com_id and g.delete_flag='normal' and g.is_valid='1' and g.com_id=? and g.org_code=?)";

		Object[] params = { userId, PubConstants.TOP_COM_ID, PubConstants.AdminOrgCode };
		List<Map<String, Object>> res = this.commonJdbc.getNamedParamJdbcTemplate().getJdbcTemplate().queryForList(sql, params);

		return !ObjectUtil.isBlank(res);
	}
}
