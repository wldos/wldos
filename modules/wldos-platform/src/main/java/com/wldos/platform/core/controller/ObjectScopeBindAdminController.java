/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.ResultCode;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoObjectScopeBind;
import com.wldos.platform.core.service.ObjectScopeBindLoader;
import com.wldos.platform.core.service.ObjectScopeBindService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 对象范围维绑定 CRUD（仅平台超管）。
 *
 * @author Yuanxi Universe
 * @version 1.0.0
 * @since 3.0
 */
@Api(tags = "对象范围维绑定")
@RestController
@RequestMapping("/admin/sys/scope/object-bind")
public class ObjectScopeBindAdminController extends EntityController<ObjectScopeBindService, WoObjectScopeBind> {

	@Autowired
	private ObjectScopeBindLoader objectScopeBindLoader;

	private void assertPlatformAdmin() {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("无权维护对象范围绑定", ResultCode.FORBIDDEN.getCode());
		}
	}

	@ApiOperation("实体字段绑定分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "20"),
			@ApiImplicitParam(name = "sorter", value = "排序", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "filter", value = "列过滤", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "entityKey", value = "实体类名", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "dimensionCode", value = "业务字段编码", dataTypeClass = String.class, paramType = "query"),
	})
	@GetMapping("")
	public PageData<WoObjectScopeBind> list(@RequestParam Map<String, Object> params) {
		this.assertPlatformAdmin();
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.appendParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		return this.service.execQueryForPage(new WoObjectScopeBind(), new WoObjectScopeBind(), pageQuery);
	}

	@Override
	protected void preAdd(WoObjectScopeBind entity) {
		this.assertPlatformAdmin();
		this.service.validateBind(entity);
	}

	@Override
	protected void preUpdate(WoObjectScopeBind entity) {
		this.assertPlatformAdmin();
		this.service.validateBind(entity);
	}

	@Override
	protected void preDelete(WoObjectScopeBind entity) {
		this.assertPlatformAdmin();
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		this.assertPlatformAdmin();
	}

	@Override
	protected void postAdd(WoObjectScopeBind entity) {
		this.objectScopeBindLoader.refresh();
	}

	@Override
	protected void postUpdate(WoObjectScopeBind entity) {
		this.objectScopeBindLoader.refresh();
	}

	@Override
	protected void postDelete(WoObjectScopeBind entity) {
		this.objectScopeBindLoader.refresh();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.objectScopeBindLoader.refresh();
	}
}
