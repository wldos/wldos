/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.List;
import java.util.Map;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.core.service.RoleService;
import com.wldos.system.vo.Role;
import com.wldos.system.core.entity.WoRole;
import com.wldos.system.vo.RoleResTree;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/role")
public class RoleController extends RepoController<RoleService, WoRole> {
	
	@GetMapping("")
	public PageableResult<Role> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		return this.service.execQueryForTree(new Role(), new WoRole(), pageQuery, Constants.TOP_ROLE_ID);
	}
	
	@GetMapping("res")
	public RoleResTree queryAuthRes(@RequestParam Map<String, String> authRole) {

		return this.service.queryAllResTreeByRoleId(Long.parseLong(authRole.get("roleId")));
	}

	@PostMapping("auth")
	public String authRole(@RequestBody Map<String, Object> resRole) {
		Long roleId = Long.parseLong(resRole.get("roleId").toString());
		List<String> resIds = (List<String>) resRole.get("resIds");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		this.service.authRole(resIds, roleId, curUserId, uip);

		return this.resJson.ok("ok");
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
			}else
				res.set(0, plat);
		}

		return res;
	}
}
