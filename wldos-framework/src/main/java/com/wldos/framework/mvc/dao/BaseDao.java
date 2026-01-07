/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wldos.framework.common.SaveOptions;
import com.wldos.framework.common.CommonOperation;

/**
 * 基础仓接口。BaseRepo 是所有数据访问对象的基础接口。相当于dao。
 * 
 * 提供统一的 CRUD 方法：
 * - 保存操作：使用 Spring Data JDBC 的 save() 方法（默认行为，不支持可选配置）
 * - 可选保存：使用 saveOrUpdate() 方法（支持 SaveOptions 配置，由 MyJdbcRepository 实现）
 * - 查询操作：使用 Spring Data JDBC 的原生 API（findById, findAll 等）
 *
 * @author 元悉宇宙
 * @date 2023/10/21
 * @version 1.0
 */
@NoRepositoryBean
public interface BaseDao<E, PK> extends PagingAndSortingRepository<E, PK> {
	
	/**
	 * 统一的保存或更新方法（推荐使用，对齐 MyBatis-Plus 设计）。
	 * 
	 * 功能特性：
	 * 1. 自动判断 insert/update（通过 @Version 或 id 查询）
	 * 2. 支持乐观锁（@Version 字段自动维护）
	 * 3. 自动填充公共字段（createTime, updateTime, createBy, updateBy 等）
	 * 4. 只写入非空字段（选择性插入/更新）
	 * 
	 * 注意：此方法由 MyJdbcRepository 实现，使用缓存的 CommonOperation 实例，性能更优。
	 *
	 * @param entity 实体
	 * @return 保存或更新后的实体对象（包含自动填充的字段、ID、版本号等）
	 */
	E saveOrUpdate(E entity);
	
	/**
	 * 统一的保存或更新方法（使用配置对象，推荐使用）。
	 * 
	 * 注意：此方法由 MyJdbcRepository 实现，使用缓存的 CommonOperation 实例，性能更优。
	 * 
	 * @param entity 实体
	 * @param options 保存选项配置
	 * @return 保存或更新后的实体对象（包含自动填充的字段、ID、版本号等）
	 */
	E saveOrUpdate(E entity, SaveOptions options);
	
	/**
	 * 统一的保存或更新方法（完整参数版本，不推荐使用）。
	 * 
	 * @deprecated 推荐使用 saveEntity(entity, SaveOptions) 方法，参数更清晰
	 * @param entity 实体
	 * @param isAutoFill 是否自动填充公共字段，默认 true
	 * @param includeNullValues 是否写入空值，默认 false（只写入非空字段）
	 * @param mergeNullValues 是否合并 null 值（更新时从数据库读取旧值填充），默认 false
	 * @return 保存或更新后的实体对象（包含自动填充的字段、ID、版本号等）
	 */
	@Deprecated
	E saveOrUpdate(E entity, boolean isAutoFill, boolean includeNullValues, boolean mergeNullValues);
	
	/**
	 * 批量保存（统一 API）。
	 * 
	 * 注意：此方法由 MyJdbcRepository 实现，使用缓存的 CommonOperation 实例，性能更优。
	 * 
	 * @param entities 实体集合
	 * @return 保存或更新后的实体集合（包含自动填充的字段、ID、版本号等）
	 */
	Iterable<E> saveOrUpdateAll(Iterable<E> entities);
	
	/**
	 * 批量保存（使用配置对象，推荐使用）。
	 * 
	 * 注意：此方法由 MyJdbcRepository 实现，使用缓存的 CommonOperation 实例，性能更优。
	 * 
	 * @param entities 实体集合
	 * @param options 保存选项配置
	 * @return 保存或更新后的实体集合（包含自动填充的字段、ID、版本号等）
	 */
	Iterable<E> saveOrUpdateAll(Iterable<E> entities, SaveOptions options);
	
	/**
	 * 批量保存（完整参数版本，不推荐使用）。
	 * 
	 * @deprecated 推荐使用 saveEntityAll(entities, SaveOptions) 方法，参数更清晰
	 * @param entities 实体集合
	 * @param isAutoFill 是否自动填充公共字段，默认 true
	 * @param includeNullValues 是否写入空值，默认 false（只写入非空字段）
	 * @param mergeNullValues 是否合并 null 值（更新时从数据库读取旧值填充），默认 false
	 * @return 操作是否成功（true=成功，false=失败）
	 */
	@Deprecated
	boolean saveOrUpdateAll(Iterable<E> entities, boolean isAutoFill, boolean includeNullValues, boolean mergeNullValues);
	
	/**
	 * 获取 CommonOperation 实例（用于其他功能）。
	 * 使用场景：
	 * - 需要直接调用 CommonOperation 的其他方法
	 * - 作为 saveOrUpdate() 等方法的后备实现
	 * 
	 * @return CommonOperation 实例，不能为 null
	 * @throws IllegalStateException 如果无法获取 CommonOperation 实例
	 */
	CommonOperation getCommonOperation();
}
