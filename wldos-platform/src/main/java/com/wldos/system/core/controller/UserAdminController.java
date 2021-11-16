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
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.core.entity.WoUser;
import com.wldos.system.core.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/user")
public class UserAdminController extends RepoController<UserService, WoUser> {

	@GetMapping("")
	public PageableResult<WoUser> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);

		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}

		return this.service.queryUserForPage(new WoUser(), pageQuery);
	}

	@GetMapping("org")
	public PageableResult<WoUser> listOrgUser(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);

		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}

		return this.service.queryUserForPage(new WoUser(), pageQuery);
	}

	@GetMapping("select")
	public PageableResult<WoUser> listSelect(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);


		return this.service.execQueryForPage(new WoUser(), pageQuery);
	}

	@GetMapping("adminMenu")
	public List<Menu> adminMenu() {
		return this.service.queryAdminMenuByUser(this.getDomain(), this.getTenantId(), this.getCurUserId());
	}
}
