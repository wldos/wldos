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
import com.wldos.system.auth.service.LoginAuthService;
import com.wldos.system.auth.vo.Login;
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.auth.vo.PasswdModifyParams;
import com.wldos.system.auth.vo.Register;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoUser;
import com.wldos.system.core.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	private final LoginAuthService loginAuthService;

	public UserAdminController(LoginAuthService loginAuthService) {
		this.loginAuthService = loginAuthService;
	}

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
		WoOrg tAdminOrg = this.service.queryTenantAdminOrg();
		pageQuery.pushParam("orgId", tAdminOrg.getId());

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

	@PostMapping("passwd4admin")
	public Login changePasswd4admin(@RequestBody PasswdModifyParams passwdModifyParams) {

		getLog().info("用户id: {} 密码修改, 修改人id：{}", passwdModifyParams.getId(), this.getCurUserId());
		Login user = this.loginAuthService.changePasswd4admin(passwdModifyParams);
		if (user == null) {
			getLog().info("{} 密码修改失败", passwdModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码修改失败，请重试！");
		}
		return user;
	}

	@PostMapping("register4admin")
	public Login addUser4admin(@RequestBody Register register) {
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		getLog().info("register= {} ", register.getLoginName());
		return this.loginAuthService.addUser4admin(this.getDomain(), register);
	}
}
