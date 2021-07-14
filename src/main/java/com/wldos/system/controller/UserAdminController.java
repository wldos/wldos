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
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.entity.WoUser;
import com.wldos.system.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理相关controller。
 *
 * @Title UserController
 * @Package com.wldos.system.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/2
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/sys/user")
public class UserAdminController extends RepoController<UserService, WoUser> {

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

		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) { // 过滤租户数据
			pageQuery.appendParam(PubConstants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}
		PageableResult<WoUser> res = this.service.queryUserForPage(new WoUser(), pageQuery);
		return resJson.ok(res);
	}

	/**
	 * 获取用户有权限的管理菜单
	 *
	 * @return
	 */
	@GetMapping("adminMenu")
	public String adminMenu() {
		List<Menu> menus = this.service.queryAdminMenuByUser(this.getCurUserId());
		return this.resJson.ok(menus);
	}
}
