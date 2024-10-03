/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.base.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.wldos.base.tools.EntityAssists;
import com.wldos.base.tools.IDGen;
import com.wldos.common.Constants;
import com.wldos.common.utils.NameConvert;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.IpUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * repo实现类，不使用事件监听或者回调钩子实现扩展的原因在于官方提到可能有等待和不确定因素。
 *
 * @author 元悉宇宙
 * @date 2023/10/23
 * @version 1.0
 */
@Slf4j
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class MyJdbcRepository<T, ID> extends SimpleJdbcRepository<T, ID> {

	private final JdbcAggregateOperations entityOperations;

	private final PersistentEntity<T, ?> entity;

	public MyJdbcRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity,
			JdbcConverter converter) {
		super(entityOperations, entity, converter);

		Assert.notNull(entityOperations, "EntityOperations must not be null.");
		Assert.notNull(entity, "Entity must not be null.");

		this.entityOperations = entityOperations;
		this.entity = entity;
	}

	@Deprecated
	public MyJdbcRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity) {
		super(entityOperations, entity);
		Assert.notNull(entityOperations, "EntityOperations must not be null.");
		Assert.notNull(entity, "Entity must not be null.");

		this.entityOperations = entityOperations;
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Transactional
	@Override
	public <S extends T> S save(S instance) {
		String key = this.getIdColNameByEntity(instance.getClass());
		Map<String, Object> params = ObjectUtils.toMap(instance);
		Object value = params.get(key);
		if (value != null) {
			Optional<T> t = this.findById((ID) value);
			if (t.isPresent()) { // 存在说明有记录，不存在说明是insert
				T old = t.get();
				// 把数据库中旧记录覆盖新记录中为null的那些域，以实现更新过滤null
				BeanUtils.copyProperties(old, instance, getNoNullProperties(instance));
				EntityAssists.beforeUpdated(instance, this.getUserId(), this.getUserIp());
			}
			else {
				EntityAssists.beforeInsert(instance, IDGen.nextId(), this.getUserId(), this.getUserIp(), true);
			}
		}
		else {
			EntityAssists.beforeInsert(instance, IDGen.nextId(), this.getUserId(), this.getUserIp(), true);
		}

		return entityOperations.save(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
	 */
	@Transactional
	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		return Streamable.of(entities).stream() //
				.map(this::save) //
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
	 */
	@Override
	public Optional<T> findById(ID id) {
		return Optional.ofNullable(entityOperations.findById(id, entity.getType()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
	 */
	@Override
	public boolean existsById(ID id) {
		return entityOperations.existsById(id, entity.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	@Override
	public Iterable<T> findAll() {
		return entityOperations.findAll(entity.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll(java.lang.Iterable)
	 */
	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		return entityOperations.findAllById(ids, entity.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	@Override
	public long count() {
		return entityOperations.count(entity.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void deleteById(ID id) { // todo 实现逻辑删
		entityOperations.deleteById(id, entity.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(T instance) { // todo 实现逻辑删
		entityOperations.delete(instance, entity.getType());
	}

	@Transactional
	@Override
	public void deleteAllById(Iterable<? extends ID> ids) { // todo 实现逻辑删
		ids.forEach(it -> entityOperations.deleteById(it, entity.getType()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
	 */
	@Transactional
	@Override
	@SuppressWarnings("unchecked")
	public void deleteAll(Iterable<? extends T> entities) { // todo 实现逻辑删
		entities.forEach(it -> entityOperations.delete(it, (Class<T>) it.getClass()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() { // todo 实现逻辑删
		entityOperations.deleteAll(entity.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort sort)
	 */
	@Override
	public Iterable<T> findAll(Sort sort) {
		return entityOperations.findAll(entity.getType(), sort);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable pageable)
	 */
	@Override
	public Page<T> findAll(Pageable pageable) {
		return entityOperations.findAll(entity.getType(), pageable);
	}

	private <E> String getIdColNameByEntity(Class<E> clazz) {
		String pKeyName = "";
		// 获取字段
		Field[] fields = clazz.getDeclaredFields();

		for (Field f : fields) {
			if (f.getAnnotation(Id.class) != null) {
				Column propRelColumn = f.getAnnotation(Column.class);
				if (propRelColumn != null) {
					pKeyName = propRelColumn.value();
				}
				else {
					pKeyName = NameConvert.humpToUnderLine(f.getName());
				}
				break;
			}  // 判断是否主键
		}

		return pKeyName;
	}

	private HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return servletRequest.getRequest();
	}

	private Long getUserId() {
		try {
			return Long.parseLong(this.getRequest().getHeader(Constants.CONTEXT_KEY_USER_ID)); // 纯请求无状态
		}
		catch (NumberFormatException e) {
			return Constants.GUEST_ID;
		}
	}

	private String getUserIp() {
		return IpUtils.getClientIp(this.getRequest());
	}

	private String[] getNoNullProperties(Object instance) {
		BeanWrapper srcBean = new BeanWrapperImpl(instance);
		PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
		Set<String> noEmptyName = new HashSet<>();
		for (PropertyDescriptor p : pds) {
			Object value = srcBean.getPropertyValue(p.getName());
			if (value != null) noEmptyName.add(p.getName());
		}
		String[] result = new String[noEmptyName.size()];
		return noEmptyName.toArray(result);
	}
}
