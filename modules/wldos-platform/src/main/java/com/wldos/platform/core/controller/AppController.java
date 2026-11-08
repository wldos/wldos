/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.utils.ObjectUtils;
import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.platform.support.rbac.DataScopeGuard;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;
import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.enums.AppOriginEnum;
import com.wldos.platform.core.enums.AppTypeEnum;

import com.wldos.platform.core.service.AppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 应用相关controller。
 * @todo 租户的小程序是应用管理的衍生，相当于开放平台，暂不支持。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "应用管理")
@RestController
@RequestMapping("admin/sys/app")
public class AppController extends EntityController<AppService, WoApp> {
	/**
	 * 查询可管理的应用列表
	 *
	 * @param params 查询参数
	 * @return 应用分页数据
	 */
	@ApiOperation(value = "应用列表", notes = "查询可管理的应用列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "appName", value = "应用名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "appType", value = "应用类型", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<WoApp> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_TENANT, WoApp.class);

		return this.service.execQueryForPage(new WoApp(), pageQuery);
	}

	/**
	 * 下拉/表格 label 用全量应用：租户管需同时看到平台目录（com_id=0）与本租户应用。
	 */
	@GetMapping("all")
	@Override
	public List<WoApp> allEntities() {
		if (!this.isMultiTenancy() || this.isAdmin(this.getUserId())) {
			return this.doFilter(this.service.findAll());
		}
		Long tenantComId = this.getWorkComId();
		Map<String, Object> platformCond = new HashMap<>();
		platformCond.put(Constants.COMMON_KEY_TENANT_COLUMN, Constants.TOP_COM_ID);
		List<WoApp> platformApps = this.service.findAllWithCond(WoApp.class, platformCond);
		if (tenantComId == null || tenantComId <= 0 || Objects.equals(tenantComId, Constants.TOP_COM_ID)) {
			return this.doFilter(platformApps);
		}
		Map<String, Object> tenantCond = new HashMap<>();
		tenantCond.put(Constants.COMMON_KEY_TENANT_COLUMN, tenantComId);
		List<WoApp> tenantApps = this.service.findAllWithCond(WoApp.class, tenantCond);
		Map<Long, WoApp> merged = new LinkedHashMap<>();
		for (WoApp app : platformApps) {
			if (app != null && app.getId() != null) {
				merged.put(app.getId(), app);
			}
		}
		for (WoApp app : tenantApps) {
			if (app != null && app.getId() != null) {
				merged.put(app.getId(), app);
			}
		}
		return this.doFilter(new ArrayList<>(merged.values()));
	}

	/**
	 * 查询域关联的应用列表
	 *
	 * @param params 查询参数
	 * @return 应用分页数据
	 */
	@ApiOperation(value = "域应用列表", notes = "查询域关联的应用列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "域名ID", dataTypeClass = Long.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "filter", value = "列筛选 JSON，是否隐藏传 mountValid：0=是、1=否", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("domain")
	public PageData<WoApp> listDomainApp(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		Long domainId = Long.parseLong(params.get("domainId").toString());
		this.assertBoundDomainAccess(domainId);
		return this.service.queryDomainAppForPage(domainId, pageQuery);
	}

	/**
	 * 可共选择的列表，用于给域预订应用
	 *
	 * @param params 查询参数
	 * @return 分页数据
	 */
	@ApiOperation(value = "可选应用列表", notes = "可共选择的列表，用于给域预订应用")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "appName", value = "应用名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "站点 id（域应用添加弹窗必传）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainMounted", value = "站点挂载状态：1=已添加，0=未添加", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "appType", value = "管理类型", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "appOrigin", value = "应用来源", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("select")
	public PageData<WoApp> listSelect(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		Object domainRaw = params.get("domainId");
		if (domainRaw != null && !ObjectUtils.isBlank(domainRaw.toString())) {
			Long domainId = Long.parseLong(domainRaw.toString().trim());
			this.assertBoundDomainAccess(domainId);
			return this.service.queryAppsForDomainSelect(domainId, pageQuery, this.getUserId());
		}

		return this.service.execQueryForPage(new WoApp(), pageQuery);
	}

	/**
	 * 应用类型枚举
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "应用类型枚举", notes = "获取应用类型枚举列表")
	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {

		boolean showPlatformType = this.isAdmin(this.getUserId())
				|| this.isPlatformAdmin(this.getTenantId());
		return (showPlatformType
				? Arrays.stream(AppTypeEnum.values())
				: Arrays.stream(AppTypeEnum.values()).filter(v -> !v.getValue().equals(Constants.ENUM_TYPE_APP_PLAT))).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}

	/**
     * 获取应用来源枚举列表（下拉列表）
     */
	@ApiOperation(value = "应用来源枚举", notes = "获取应用来源枚举列表")
    @GetMapping("/origins")
    public List<Map<String, String>> getAppOrigins() {
		return Arrays.stream(AppOriginEnum.values()).map(item -> {
			Map<String, String> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
    }

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoApp entity = this.service.findById(id);
		if (entity != null) {
			this.assertEntityRowScope(entity);
		}
		return entity;
	}

	@Override
	protected void preAdd(WoApp entity) {
		this.assertNotSiteAdminForApp();
		this.applyWriteScope(entity, WriteScopeTarget.TENANT_AXIS);
	}

	@Override
	protected void preUpdate(WoApp entity) {
		this.assertNotSiteAdminForApp();
		if (entity != null && entity.getId() != null) {
			WoApp existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				if (!this.isAdmin(this.getUserId()) && this.isMultiTenancy()) {
					DataScopeGuard.assertComIdImmutable(existing.getComId(), entity.getComId());
				}
			}
		}
	}

	@Override
	protected void preDelete(WoApp entity) {
		this.assertNotSiteAdminForApp();
		if (entity != null && entity.getId() != null) {
			WoApp existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		this.assertNotSiteAdminForApp();
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			WoApp existing = this.service.findById(Long.parseLong(raw.toString()));
			if (existing != null) {
				this.assertEntityRowScope(existing);
			}
		}
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