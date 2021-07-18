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
import com.wldos.support.util.constant.PubConstants;
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
 * 标准实体基础controller，用于收口业务系统请求、响应边界，支持领域对象和聚合根操作。
 *
 * @param <S> 实体service
 * @param <E> 这可能是一个常规的拥有一对一数据库表的实体，也可能是基于领域的聚合根实体
 * @author wldos
 * @date 2021-04-16
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class RepoController<S extends BaseService, E> extends BaseController {

	@Autowired
	protected ResultJson<E> resJson;

	@Autowired
	protected S service;

	@PostMapping("add")
	public String add(@RequestBody E entity) {
		EntityAssists.beforeInsert(entity, this.nextId(), this.getCurUserId(), this.getUserIp(), false);
		service.insertSelective(entity);
		return resJson.ok(Boolean.TRUE);
	}

	@GetMapping("get")
	public String get(@RequestParam long id) {
		Object o = service.findById(id);
		return resJson.ok(o);
	}

	@PostMapping("update")
	public String update(@RequestBody E entity) {
		EntityAssists.beforeUpdated(entity, this.getCurUserId(), this.getUserIp());
		service.update(entity);
		return resJson.ok(Boolean.TRUE);
	}

	@DeleteMapping("delete")
	public String remove(@RequestBody E entity) {
		this.service.delete(entity);
		return resJson.ok(Boolean.TRUE);
	}

	@DeleteMapping("deletes")
	public String removeIds(@RequestBody Map<String, Object> jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			service.deleteByIds(((List<Object>) ids).toArray());
		}

		return resJson.ok(Boolean.TRUE);
	}

	@GetMapping("all")
	public String all() {
		List<E> list = null;
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			Map<String, Object> cond = new HashMap<>();
			Class<E> clazz = this.service.getEntityClass(1);
			cond.put(clazz == WoCompany.class ? "id" : PubConstants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
			list = this.service.findAllWithCond(clazz, cond);
		}
		else
			list = service.findAll();

		return resJson.ok(list);
	}

	/**
	 * 不带参数的分页查询，标准实体。带参数的查询，请参照ResourceController。
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
	@GetMapping("page")
	public String list(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		Pageable page = PageRequest.of(pageQuery.getCurrent() - 1, pageQuery.getPageSize(), pageQuery.getSorter());
		Page<E> res = service.findAll(page);
		return resJson.ok(res);
	}
}
