/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.support.controller.web.ResultJson;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.entity.WoCompany;
import org.slf4j.Logger;

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
 * @param <EntityService>
 * @param <Entity> 这可能是一个常规的拥有一对一数据库表的实体，也可能是基于领域的聚合根实体表
 * @author wldos
 * @date 2021-04-16
 * @version 1.0
 */
public abstract class RepoController<EntityService extends BaseService, Entity> extends BaseController {

	protected Logger log = this.getLog();

	@Autowired
	protected ResultJson<Entity> resJson;

	@Autowired
	protected EntityService service;

	@PostMapping("add")
	public String add(@RequestBody Entity entity) {
		EntityTools.setInsertInfo(entity, this.nextId(), this.getCurUserId(), this.getUserIp(), false);
		service.insertSelective(entity);
		return resJson.ok(Boolean.valueOf(true));
	}

	@GetMapping("get")
	public String get(@RequestParam long id) {
		Object o = service.findById(id);
		return resJson.ok(o);
	}

	@PostMapping("update")
	public String update(@RequestBody Entity entity) {
		EntityTools.setUpdatedInfo(entity, this.getCurUserId(), this.getUserIp());
		service.update(entity);
		return resJson.ok(Boolean.valueOf(true));
	}

	@DeleteMapping("delete")
	public String remove(@RequestBody Entity entity) {
		this.service.delete(entity);
		return resJson.ok(Boolean.valueOf(true));
	}

	@DeleteMapping("deletes")
	public String removeIds(@RequestBody Map jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			service.deleteByIds(((List<Object>) ids).toArray());
		}

		return resJson.ok(Boolean.valueOf(true));
	}

	@GetMapping("all")
	public String all() {
		List<Entity> list = null;
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			Map<String, Object> cond = new HashMap<>();
			Class<Entity> clazz = this.service.getEntityClass(1);
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
	 * @param params
	 * @return
	 */
	@GetMapping("page")
	public String list(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		Pageable page = PageRequest.of(pageQuery.getCurrent() - 1, pageQuery.getPageSize(), pageQuery.getSorter());
		Page<Entity> res = service.findAll(page);
		return resJson.ok(res);
	}
}
