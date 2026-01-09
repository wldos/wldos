/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;

import com.wldos.framework.mvc.service.EntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 标准实体基础controller，用于收口业务系统请求、响应边界。
 * 非实体controller基础上增加CRUD方法。
 *
 * @param <S> 实体service
 * @param <E> 拥有数据库表的实体
 * @author wldos
 * @date 2021-04-16
 * @version 1.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class EntityController<S extends EntityService, E> extends NonEntityController<S> {


	@PostMapping("add")
	public String add(@RequestBody E entity) {
		this.preAdd(entity);
		service.saveOrUpdate(entity);  // 统一使用 saveOrUpdate() 方法，自动判断 insert/update
		this.postAdd(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 新增实体时，对实体二次处理的hook
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preAdd(E entity) {
	}

	/**
	 * 新增完成后，触发的操作，比如刷新缓存
	 */
	protected void postAdd(E entity) {
	}

	@PostMapping("update")
	public String update(@RequestBody E entity) {
		this.preUpdate(entity);
		service.saveOrUpdate(entity);  // 统一使用 saveOrUpdate() 方法，自动判断 insert/update
		this.postUpdate(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 更新实体时，对实体二次处理的hook
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preUpdate(E entity) {
	}

	/**
	 * 更新完成后，触发的操作，比如刷新缓存
	 */
	protected void postUpdate(E entity) {
	}

	@DeleteMapping("delete")
	public String remove(@RequestBody E entity) {
		this.preDelete(entity);
		this.service.delete(entity);
		this.postDelete(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 删除实体时，对实体二次处理的hook
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preDelete(E entity) {
	}

	/**
	 * 删除完成后，触发的操作，比如刷新缓存
	 */
	protected void postDelete(E entity) {
	}

	/**
	 * 批量物理删
	 * @param jsonObject 批量参数 Map<String, Object> key="ids"
	 */
	@DeleteMapping("deletes")
	public String removeIds(@RequestBody Map<String, Object> jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			List<Object> objects = (List<Object>) ids;
			this.preDeletes(objects);
			service.deletePhysicalByIds(objects.toArray());

			this.postDeletes(objects);
		}

		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 批量删除实体时，对实体二次处理的hook
	 *
	 * @param ids 实例ids
	 */
	protected void preDeletes(List<Object> ids) {
	}

	/**
	 * 批量删除完成后，触发的操作，比如刷新缓存
	 */
	protected void postDeletes(List<Object> ids) {
	}

	@GetMapping("get")
	public Object get(@RequestParam long id) {
		return service.findById(id);
	}

	@GetMapping("all")
	public List<E> all() {
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getUserId())) {
			Map<String, Object> cond = new HashMap<>();
			Class<E> clazz = this.service.getEntityClass(1);
			cond.put(clazz.getSimpleName().equals(Constants.CLASS_NAME_COMPANY) ? "id" : Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
			return this.doFilter(this.service.findAllWithCond(clazz, cond));
		}
		else
			return this.doFilter(service.findAll());
	}

	/**
	 * 结果集过滤器
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	protected List<E> doFilter(List<E> res) {
		return res;
	}

	/**
	 * 不带参数的分页查询，标准实体。带参数的查询，请参照ResourceController。
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
	@Deprecated
	@GetMapping("page")
	public Page<E> list(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		Pageable page = PageRequest.of(pageQuery.getCurrent() - 1, pageQuery.getPageSize(), pageQuery.getSorter());

		return service.findAll(page);
	}
}