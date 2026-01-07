/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.framework.common.SaveOptions;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 实体基类service，NonEntity基础上添加 CRUD 方法，可以直接处理与数据库的交互。
 * 有唯一确定实体的公共顶层service，实现基础curd操作，固定service层与entityRepo层。
 *
 * @author 元悉宇宙
 * @date 2021/5/5
 * @version 1.0
 * @param <R> 实体仓库
 * @param <E> 实体Bean
 * @param <PK> 实体主键类型
 *
 */
@Slf4j
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public abstract class EntityService<R extends BaseDao<E, PK>, E, PK> extends NonEntityService {
	/**
	 * 实体Repo
	 */
	@Autowired
	@SuppressWarnings({ "all" })
	protected R entityRepo;

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
	 * 统一的保存或更新方法（推荐使用，对齐 CommonOperation API）。
	 * 
	 * 功能特性：
	 * 1. 自动判断 insert/update（通过 @Version 或 id 查询）
	 * 2. 支持乐观锁（@Version 字段自动维护）
	 * 3. 自动填充公共字段（createTime, updateTime, createBy, updateBy 等）
	 * 4. 只写入非空字段（选择性插入/更新）
	 * 5. 支持合并 null 值（更新时从数据库读取旧值填充）
	 *
	 * @param entity 实体bean
	 * @return 保存或更新后的实体对象（包含自动填充的字段、ID、版本号等）
	 */
	public E saveOrUpdate(E entity) {
		return this.entityRepo.saveOrUpdate(entity);
	}

	/**
	 * 统一的保存或更新方法（使用配置对象，推荐使用）。
	 * 
	 * @param entity 实体bean
	 * @param options 保存选项配置
	 * @return 保存或更新后的实体对象（包含自动填充的字段、ID、版本号等）
	 */
	public E saveOrUpdate(E entity, SaveOptions options) {
		return this.entityRepo.saveOrUpdate(entity, options);
	}

	/**
	 * 物理删除
	 *
	 * @param entity 实体bean
	 */
	public void delete(E entity) {
		entityRepo.delete(entity);
	}

	protected void preDelete(E entity) {

	}

	/**
	 * 批量物理删除
	 *
	 * @param ids 主键
	 */
	public void deletePhysicalByIds(Object... ids) {

		for (Object o : ids) {
			E entity = this.findById((PK) o);
			if (entity != null) {
				this.commonOperate.deleteByIds(entity, ids, false);
				break;
			}
		}
	}

	/**
	 * 根据Id删除,仅支持逻辑删
	 *
	 * @param id 主键id
	 */
	public void deleteById(PK id) {
		Object[] params = { id };
		this.deleteByIds(params);
	}

	/**
	 * 批量删除,仅支持逻辑删
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
	 * 批量保存或更新（统一 API，推荐使用，对齐 CommonOperation API）。
	 * 
	 * @param entities 实体bean迭代器
	 * @return 保存或更新后的实体集合（包含自动填充的字段、ID、版本号等）
	 */
	public Iterable<E> saveOrUpdateAll(Iterable<E> entities) {
		return this.entityRepo.saveOrUpdateAll(entities);
	}

	/**
	 * 批量保存或更新（使用配置对象，推荐使用）。
	 * 
	 * @param entities 实体bean迭代器
	 * @param options 保存选项配置
	 * @return 保存或更新后的实体集合（包含自动填充的字段、ID、版本号等）
	 */
	public Iterable<E> saveOrUpdateAll(Iterable<E> entities, SaveOptions options) {
		return this.entityRepo.saveOrUpdateAll(entities, options);
	}

	/**
	 * 批量保存（统一 API，参考 MyBatis-Plus 设计）。
	 * 
	 * @deprecated 推荐使用 saveOrUpdateAll() 方法（统一 API），此方法保留用于向后兼容
	 * @param entities 实体bean迭代器
	 * @return 保存或更新后的实体集合（包含自动填充的字段、ID、版本号等）
	 */
	@Deprecated
	public Iterable<E> saveAll(Iterable<E> entities) {
		return saveOrUpdateAll(entities);
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

	protected void preDeletes(List<Object> ids) {

	}
}