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
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoScopeDimension;
import com.wldos.platform.core.service.ScopeDimensionLoader;
import com.wldos.platform.core.service.ScopeDimensionService;
import com.wldos.platform.core.support.ScopeDimensionCatalogHelper;
import com.wldos.platform.core.vo.ScopeDimensionBatchBody;
import com.wldos.platform.core.vo.ScopeDimensionBatchResultVO;

import io.github.wldos.platform.support.rbac.EffectiveScopeCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 范围维注册表 CRUD（仅平台超管）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "范围维维护")
@RestController
@RequestMapping("/admin/sys/scope/dimension")
public class ScopeDimensionAdminController extends EntityController<ScopeDimensionService, WoScopeDimension> {

	@Autowired
	private ScopeDimensionLoader scopeDimensionLoader;

	private void assertPlatformAdmin() {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("无权维护范围维", ResultCode.FORBIDDEN.getCode());
		}
	}

	private void refreshCatalogRuntime() {
		this.scopeDimensionLoader.refresh();
		EffectiveScopeCache.invalidateAll(this.cache);
	}

	@ApiOperation("业务字段分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "20"),
			@ApiImplicitParam(name = "sorter", value = "排序", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "filter", value = "列过滤", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "dimensionCode", value = "编码", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "dimensionName", value = "名称", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "enabled", value = "启用：1/0", dataTypeClass = String.class, paramType = "query"),
	})
	@GetMapping("")
	public PageData<WoScopeDimension> list(@RequestParam Map<String, Object> params) {
		this.assertPlatformAdmin();
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.appendParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		Sort sorter = pageQuery.getSorter();
		if (sorter == null || sorter.isUnsorted()) {
			pageQuery.pushSort(new String[][] { { "sortOrder", "asc" }, { "id", "asc" } });
		}
		return this.service.execQueryForPage(new WoScopeDimension(), new WoScopeDimension(), pageQuery);
	}

	@Override
	protected void preAdd(WoScopeDimension entity) {
		this.assertPlatformAdmin();
		normalizeScopeUiTier(entity);
	}

	@Override
	protected void preUpdate(WoScopeDimension entity) {
		this.assertPlatformAdmin();
		if (entity == null || entity.getId() == null) {
			throw new BaseException("记录不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoScopeDimension existing = this.service.findById(entity.getId());
		if (existing != null && ScopeDimensionCatalogHelper.isEnabled(existing.getEnabled())
				&& ScopeDimensionCatalogHelper.ENABLED_NO.equals(entity.getEnabled())) {
			this.service.assertDisableAllowed(existing);
		}
		normalizeScopeUiTier(entity);
	}

	@Override
	protected void preDelete(WoScopeDimension entity) {
		this.assertPlatformAdmin();
		if (entity == null || entity.getId() == null) {
			throw new BaseException("记录不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		this.service.assertDeletable(this.service.findById(entity.getId()));
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		this.assertPlatformAdmin();
		if (ids == null) {
			return;
		}
		for (Object id : ids) {
			WoScopeDimension probe = this.service.findById(Long.valueOf(String.valueOf(id)));
			this.service.assertDeletable(probe);
		}
	}

	@Override
	protected void postAdd(WoScopeDimension entity) {
		refreshCatalogRuntime();
	}

	@Override
	protected void postUpdate(WoScopeDimension entity) {
		refreshCatalogRuntime();
	}

	@Override
	protected void postDelete(WoScopeDimension entity) {
		refreshCatalogRuntime();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		refreshCatalogRuntime();
	}

	@ApiOperation("批量登记业务字段（每种字典类型一行）")
	@PostMapping("batch")
	public Result<ScopeDimensionBatchResultVO> batchRegister(@RequestBody ScopeDimensionBatchBody body) {
		this.assertPlatformAdmin();
		ScopeDimensionBatchResultVO result = this.service.batchRegisterFromDict(body);
		if (result.getCreatedCount() > 0) {
			refreshCatalogRuntime();
		}
		return Result.ok(result);
	}

	private static void normalizeScopeUiTier(WoScopeDimension entity) {
		if (entity == null) {
			return;
		}
		if (ScopeDimensionCatalogHelper.isBuiltinFilterDimension(entity)
				|| !ScopeDimensionCatalogHelper.isDictValueSource(entity.getValueSource())) {
			entity.setScopeUiTier(ScopeDimensionCatalogHelper.SCOPE_TIER_GLOBAL);
			return;
		}
		if (!StringUtils.hasText(entity.getScopeUiTier())) {
			entity.setScopeUiTier(ScopeDimensionCatalogHelper.SCOPE_TIER_CUSTOM);
		}
	}
}
