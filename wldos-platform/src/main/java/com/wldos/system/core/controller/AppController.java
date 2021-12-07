/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.support.Constants;
import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.system.core.entity.WoApp;
import com.wldos.system.core.service.AppService;
import com.wldos.system.enums.AppTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/app")
public class AppController extends RepoController<AppService, WoApp> {
	
	@GetMapping("")
	public PageableResult<WoApp> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}

		return this.service.execQueryForPage(new WoApp(), pageQuery);
	}
	
	@GetMapping("domain")
	public PageableResult<WoApp> listDomainApp(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);

		return  this.service.queryAppForPage(pageQuery);
	}
	
	@GetMapping("select")
	public PageableResult<WoApp> listSelect(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.pushFilter(Constants.COMMON_KEY_APP_TYPE, AppTypeEnum.APP.toString(), AppTypeEnum.PRIVATE.toString());
		}

		return this.service.execQueryForPage(new WoApp(), pageQuery);
	}
	
	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {

		return (this.isPlatformAdmin(this.getTenantId()) ?
				Arrays.stream(AppTypeEnum.values())
				: Arrays.stream(AppTypeEnum.values()).filter(v -> !v.getValue().equals(Constants.ENUM_TYPE_APP_PLAT))).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}

	@Override
	protected void preAdd(WoApp entity) {
		entity.setComId(this.getTenantId());
	}

	@Override
	protected void postAdd(WoApp entity) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoApp entity) {
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoApp entity) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}
}
