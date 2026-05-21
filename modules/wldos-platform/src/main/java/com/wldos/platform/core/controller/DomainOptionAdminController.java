/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.controller;

import java.util.Map;

import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoDomainOption;
import com.wldos.platform.core.service.DomainOptionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 域级系统选项（后台）：维护 {@code wo_domain_option}，用于按站点覆盖全局 {@code wo_options}。
 */
@Api(tags = "域级系统选项（后台）")
@RestController
@RequestMapping("admin/sys/domainOption")
public class DomainOptionAdminController extends EntityController<DomainOptionService, WoDomainOption> {

	@ApiOperation(value = "域级选项分页", notes = "按域、key 过滤；非超级管理员自动租户隔离")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", value = "当前页码", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query"),
			@ApiImplicitParam(name = "domainId", value = "站点域 ID", paramType = "query"),
			@ApiImplicitParam(name = "optionKey", value = "选项键", paramType = "query"),
	})
	@GetMapping("")
	public PageData<WoDomainOption> list(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		if (!this.isAdmin(this.getUserId())) {
			this.applyTenantFilter(pageQuery);
		}
		return this.service.execQueryForPage(new WoDomainOption(), new WoDomainOption(), pageQuery);
	}

	@Override
	protected void preAdd(WoDomainOption entity) {
		if (entity.getComId() == null) {
			entity.setComId(this.getTenantId());
		}
	}

	@Override
	protected void preUpdate(WoDomainOption entity) {
		if (entity.getComId() == null) {
			entity.setComId(this.getTenantId());
		}
	}
}
