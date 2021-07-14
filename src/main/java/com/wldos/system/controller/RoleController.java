/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.controller;

import java.util.List;
import java.util.Map;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.service.RoleService;
import com.wldos.system.vo.Role;
import com.wldos.system.entity.WoRole;
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
 * @Title RoleController
 * @Package com.system.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/2
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/sys/role")
public class RoleController extends RepoController<RoleService, WoRole> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params
	 * @return
	 */
	@GetMapping("")
	public String listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		PageableResult<Role> res = this.service.execQueryForTree(new Role(), new WoRole(), pageQuery, PubConstants.TOP_ROLE_ID);
		return resJson.ok(res);
	}

	/**
	 * 查询某个角色授予的资源权限（自有和继承-上查三代）
	 *
	 * @param authRole
	 * @return
	 */
	@GetMapping("res")
	public String queryAuthRes(@RequestParam Map<String, String> authRole) {

		// 查询当前角色关联的资源列表 合并 父级角色关联的资源列表
		RoleResTree roleResTree = this.service.queryAllResTreeByRoleId(Long.parseLong(authRole.get("roleId")));

		return this.resJson.ok(roleResTree);
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
}
