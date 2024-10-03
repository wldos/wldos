/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.framework.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wldos.base.tools.EntityAssists;
import com.wldos.framework.repo.BaseRepo;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
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
public abstract class RepoService<R extends BaseRepo<E, PK>, E, PK> extends AbstractService {
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
	public void save(E entity, boolean isAutoFill) {
		if (isAutoFill)
			EntityAssists.beforeInsert(entity, this.nextId(), this.commonOperate.getUserId(), this.commonOperate.getUserIp(), true);
		entityRepo.save(entity);
	}

	/**
	 * 批量保存
	 *
	 * @param entities 实体bean迭代器
	 */
	public void saveAll(Iterable<E> entities, boolean isAutoFill) {
		if (isAutoFill) {
			Long uId = this.commonOperate.getUserId();
			String uip = this.commonOperate.getUserIp();
			entities.forEach(entity -> EntityAssists.beforeInsert(entity, this.nextId(), uId, uip, true));
		}
		this.entityRepo.saveAll(entities);
	}

	/**
	 * 物理删除
	 *
	 * @param entity 实体bean
	 */
	public void delete(E entity) {
		entityRepo.delete(entity);
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
	 * 有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entity 实体
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public Long insertSelective(E entity, boolean isAutoFill) {
		return this.commonOperate.dynamicInsertByEntity(entity, isAutoFill);
	}

	/**
	 * 批量有选择地insert记录，空值不插入(采用数据库可能存在的默认值)。实现了mybatis mapper能力。
	 *
	 * @param entities 多个实体
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public void insertSelectiveAll(Iterable<E> entities, boolean isAutoFill) {
		this.commonOperate.dynamicBatchInsertByEntities((List<E>) entities, isAutoFill);
	}

	/**
	 * 根据实体属性更新，属性为空值的Long类型不更新。
	 *
	 * @param entity 实体bean
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public void update(E entity, boolean isAutoFill) {
		this.commonOperate.dynamicUpdateByEntity(entity, isAutoFill);
	}

	/**
	 * 批量更新
	 *
	 * @param entities 实体bean迭代器
	 * @param isAutoFill 是否自动填充公共字段，不存在或需要手动设置公共字段时设置为false，比mybatis-plus更快捷
	 */
	public void updateAll(Iterable<E> entities, boolean isAutoFill) {
		this.commonOperate.dynamicBatchUpdateByEntities((List<E>) entities, isAutoFill);
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
}