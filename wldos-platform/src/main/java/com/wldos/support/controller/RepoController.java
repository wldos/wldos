/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.support.controller.web.ResultJson;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.core.entity.WoCompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 标准实体基础controller。
 *
 * @param <S> 实体service
 * @param <E> 这可能是一个常规的拥有一对一数据库表的实体，也可能是基于领域的聚合根实体
 * @author wldos
 * @date 2021-04-16
 * @version 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class RepoController<S extends BaseService, E> extends BaseController {

	@Autowired
	protected ResultJson resJson;

	@Autowired
	protected S service;

	@PostMapping("add")
	public String add(@RequestBody E entity) {
		EntityAssists.beforeInsert(entity, this.nextId(), this.getCurUserId(), this.getUserIp(), false);
		this.preAdd(entity);
		service.insertSelective(entity);
		this.postAdd(entity);
		return resJson.ok(Boolean.TRUE);
	}

	protected void preAdd(E entity) {}

	protected void postAdd(E entity) {}

	@GetMapping("get")
	public Object get(@RequestParam long id) {
		return service.findById(id);
	}

	@PostMapping("update")
	public String update(@RequestBody E entity) {
		EntityAssists.beforeUpdated(entity, this.getCurUserId(), this.getUserIp());
		this.preUpdate(entity);
		service.update(entity);
		this.postUpdate(entity);
		return resJson.ok(Boolean.TRUE);
	}

	protected void preUpdate(E entity) {}

	protected void postUpdate(E entity) {}

	@DeleteMapping("delete")
	public String remove(@RequestBody E entity) {
		this.preDelete(entity);
		this.service.delete(entity);
		this.postDelete(entity);
		return resJson.ok(Boolean.TRUE);
	}

	protected void preDelete(E entity) {}

	protected void postDelete(E entity) {}

	@DeleteMapping("deletes")
	public String removeIds(@RequestBody Map<String, Object> jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			List<Object> objects = (List<Object>) ids;
			this.preDeletes(objects);
			service.deleteByIds(objects.toArray());

			this.postDeletes(objects);
		}

		return resJson.ok(Boolean.TRUE);
	}

	protected void preDeletes(List<Object> ids) {}

	protected void postDeletes(List<Object> ids) {}

	@GetMapping("all")
	public List<E> all() {
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			Map<String, Object> cond = new HashMap<>();
			Class<E> clazz = this.service.getEntityClass(1);
			cond.put(clazz == WoCompany.class ? "id" : Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
			return this.doFilter(this.service.findAllWithCond(clazz, cond));
		}
		else
			return this.doFilter(service.findAll());
	}

	protected List<E> doFilter(List<E> res) { return res;}

	@Deprecated
	@GetMapping("page")
	public Page<E> list(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		Pageable page = PageRequest.of(pageQuery.getCurrent() - 1, pageQuery.getPageSize(), pageQuery.getSorter());

		return service.findAll(page);
	}
}
