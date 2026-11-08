/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.platform.support.rbac.DataScopeGuard;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.WoArchitecture;
import com.wldos.platform.core.enums.ArchTypeEnum;

import com.wldos.platform.core.service.ArchitectureService;
import com.wldos.platform.core.vo.Architecture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 体系结构相关controller。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "体系结构管理")
@RestController
@RequestMapping("admin/sys/arch")
public class ArchitectureController extends EntityController<ArchitectureService, WoArchitecture> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询参数
	 * @return 体系结构列表
	 */
	@ApiOperation(value = "体系结构列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "archName", value = "体系名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "archType", value = "体系类型", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<Architecture> listQuery(@RequestParam Map<String, Object> params) {
		this.assertNotSiteAdminForArch();
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_TENANT, WoArchitecture.class);

		return this.service.execQueryForTree(new Architecture(), new WoArchitecture(), pageQuery, Constants.TOP_ARCH_ID);
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		this.assertNotSiteAdminForArch();
		WoArchitecture arch = this.service.findById(id);
		if (arch != null) {
			this.assertEntityRowScope(arch);
		}
		return arch;
	}

	@Override
	protected void preUpdate(WoArchitecture entity) {
		this.assertNotSiteAdminForArch();
		if (entity != null && entity.getId() != null) {
			WoArchitecture existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				if (!this.isAdmin(this.getUserId()) && this.isMultiTenancy()) {
					DataScopeGuard.assertComIdImmutable(existing.getComId(), entity.getComId());
				}
			}
		}
	}

	@Override
	protected void preDelete(WoArchitecture entity) {
		this.assertNotSiteAdminForArch();
		if (entity != null && entity.getId() != null) {
			WoArchitecture existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		this.assertNotSiteAdminForArch();
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			WoArchitecture existing = this.service.findById(Long.parseLong(raw.toString()));
			if (existing != null) {
				this.assertEntityRowScope(existing);
			}
		}
	}

	/**
	 * 新增体系时按操作者层级补默认归属（平台超管落 comId=0，租户/站点管理员落其主公司），与
	 * {@code OrgController}/{@code RoleController}/{@code AppController} 统一写法。
	 *
	 * @param entity 体系实体
	 */
	@Override
	protected void preAdd(WoArchitecture entity) {
		this.assertNotSiteAdminForArch();
		this.applyWriteScope(entity, WriteScopeTarget.TENANT_AXIS);
	}

	/**
	 * 查询体系时追加平台根体系
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoArchitecture> doFilter(List<WoArchitecture> res) {
		Long userId = this.getUserId();
		if (!this.isAdmin(userId) && !this.isPlatformAdmin(this.getTenantId())) {
			res.removeIf(a -> a.getComId() == null || a.getComId() == Constants.TOP_COM_ID);
		}

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoArchitecture plat = new WoArchitecture(Constants.TOP_ARCH_ID, "平台");
			if (res.isEmpty()) {
				res.add(plat);
			}
			else{
				res.add(plat);
				res.sort(Comparator.comparing(WoArchitecture::getId));
			}
		}

		return res;
	}

	/**
	 * 组织体系结构类型枚举，也作组织结构类型，不同于组织机构类型(ComTypeEnum)。。
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "体系结构类型枚举", notes = "获取组织体系结构类型枚举列表")
	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {
		this.assertNotSiteAdminForArch();
		return Arrays.stream(ArchTypeEnum.values()).map(item -> {
			Map<String, Object> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}
}
