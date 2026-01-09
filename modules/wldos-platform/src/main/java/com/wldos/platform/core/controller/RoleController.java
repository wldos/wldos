/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.service.RoleService;
import com.wldos.platform.core.vo.Role;

import com.wldos.platform.core.vo.RoleResTree;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 角色相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "角色管理")
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/role")
public class RoleController extends EntityController<RoleService, WoRole> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询参数
	 * @return 分页列表页
	 */
	@ApiOperation(value = "角色列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "roleName", value = "角色名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageableResult<Role> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Role(), new WoRole(), pageQuery, Constants.TOP_ROLE_ID);
	}

	/**
	 * 查询某个角色授予的资源权限（自有和继承-上查三代）
	 *
	 * @param authRole 授权角色
	 * @return 授权角色
	 */
	@ApiOperation(value = "角色资源权限", notes = "查询某个角色授予的资源权限（自有和继承-上查三代）")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleId", value = "角色ID", dataTypeClass = String.class, paramType = "query", required = true)
	})
	@GetMapping("res")
	public RoleResTree queryAuthRes(@RequestParam Map<String, String> authRole) {

		// 查询当前角色关联的资源列表 合并 父级角色关联的资源列表
		return this.service.queryAllResTreeByRoleId(Long.parseLong(authRole.get("roleId")));
	}

	@ApiOperation(value = "角色授权", notes = "给角色授权资源")
	@PostMapping("auth")
	public Result authRole(@ApiParam(value = "角色资源授权参数", required = true) @RequestBody Map<String, Object> resRole) {
		Long roleId = Long.parseLong(resRole.get("roleId").toString());
		List<String> resIds = (List<String>) resRole.get("resIds");
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		this.service.authRole(resIds, roleId, curUserId, uip);
		this.refreshAuth();

		return Result.ok("ok");
	}

	@Override
	protected void postAdd(WoRole role) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoRole role) {
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoRole role) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}

	/**
	 * 查询角色列表时追加平台根角色
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoRole> doFilter(List<WoRole> res) {

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoRole plat = new WoRole(Constants.TOP_ROLE_ID, "根角色");
			if (res.isEmpty()) {
				res.add(plat);
			}
			else{
				res.add(plat);
				res.sort(Comparator.comparing(WoRole::getId));
			}
		}

		return res;
	}
}