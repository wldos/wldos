/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;

import com.wldos.platform.auth.service.LoginAuthService;
import com.wldos.platform.auth.vo.Login;
import com.wldos.platform.auth.vo.PasswdModifyParams;
import com.wldos.platform.auth.vo.Register;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.platform.support.resource.vo.Menu;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.service.UserService;

import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 用户管理相关controller。
 * 租户管理员不具备删除用户权限，这是因为注销属于用户权力，租户不能删除平台用户。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "用户管理（后台）")
@RestController
@RequestMapping("admin/sys/user")
public class UserAdminController extends EntityController<UserService, WoUser> {

	private final LoginAuthService loginAuthService;

	public UserAdminController(LoginAuthService loginAuthService) {
		this.loginAuthService = loginAuthService;
	}

	/**
	 * 可管理的用户列表
	 *
	 * @param params 查询参数
	 * @return 用户列表
	 */
	@ApiOperation(value = "用户列表", notes = "可管理的用户列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "email", value = "邮箱（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageableResult<WoUser> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		this.applyTenantFilter(pageQuery);

		return this.service.queryUserForPage(new WoUser(), pageQuery);
	}

	/**
	 * 查询组织下的用户列表
	 *
	 * @param params 查询参数
	 * @return 用户列表
	 */
	@ApiOperation(value = "组织用户列表", notes = "查询组织下的用户列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("org")
	public PageableResult<WoUser> listOrgUser(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		return this.service.queryUserForPage(new WoUser(), pageQuery);
	}

	/**
	 * 查询租户下的管理员列表
	 *
	 * @param params 查询参数,根据uComId(本租户id)、orgId(租户管理系统用户组,隶属于comId=0的平台)查询租户管理用户组下归属本租户的人员（即本租户的管理员）
	 * @return 租户管理员列表
	 */
	@ApiOperation(value = "租户管理员列表", notes = "查询租户下的管理员列表，根据uComId(本租户id)、orgId(租户管理系统用户组)查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "uComId", value = "用户公司ID（本租户id）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("com")
	public PageableResult<WoUser> listComAdminUser(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		// 查询租户管理员组织id，并作为查询参数, 注意前端要传递的租户id是uComId（用户公司），而非comId（组织公司）
		WoOrg tAdminOrg = this.service.queryTenantAdminOrg();
		pageQuery.pushParam("orgId", tAdminOrg.getId());

		return this.service.queryUserForPage(new WoUser(), pageQuery);
	}

	/**
	 * 可添加的用户列表
	 *
	 * @param params 查询参数
	 * @return 可添加用户
	 */
	@ApiOperation(value = "可选用户列表", notes = "可添加的用户列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("select")
	public PageableResult<WoUser> listSelect(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		// @todo 应该过滤掉平台管理用户组
		return this.service.execQueryForPage(new WoUser(), pageQuery);
	}

	/**
	 * 获取当前用户有权限的管理菜单
	 *
	 * @return 管理菜单
	 */
	@ApiOperation(value = "当前用户管理菜单", notes = "获取当前用户有权限的管理菜单")
	@GetMapping("adminMenu")
	public List<Menu> adminMenu() {
		return this.service.queryAdminMenuByUser(this.getDomainId(), this.getTenantId(), this.getUserId());
	}

	/**
	 * 获取指定用户有权限的管理菜单
	 *
	 * @return 管理菜单
	 */
	@ApiOperation(value = "指定用户管理菜单", notes = "获取指定用户有权限的管理菜单")
	@GetMapping("adminMenu/{userId}")
	public List<Menu> adminMenuByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
		return this.service.queryAdminMenuByUser(this.getDomainId(), this.getTenantId(), userId);
	}

	/**
	 * 管理员后台设置新密码，直接覆盖原密码
	 *
	 * @param passwdModifyParams 密码修改参数
	 * @return 密码修改结果
	 */
	@ApiOperation(value = "管理员修改密码", notes = "管理员后台设置新密码，直接覆盖原密码")
	@PostMapping("passwd4admin")
	public Login changePasswd4admin(@ApiParam(value = "密码修改参数", required = true) @Valid @RequestBody PasswdModifyParams passwdModifyParams) {

		getLog().info("用户id: {} 密码修改, 修改人id：{}", passwdModifyParams.getId(), this.getUserId());
		Login user = this.loginAuthService.changePasswd4admin(passwdModifyParams);
		if (user == null) {
			getLog().warn("{} 密码修改失败", passwdModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码修改失败，请重试！");
		}
		return user;
	}

	/**
	 * 管理员后台添加新用户
	 * 租户管理员添加后，还需要从组织管理添加人员到组织，才能在用户管理看到用户
	 *
	 * @param register 用户注册信息
	 * @return 注册结果
	 */
	@ApiOperation(value = "管理员添加用户", notes = "管理员后台添加新用户")
	@PostMapping("register4admin")
	public Login addUser4admin(@ApiParam(value = "用户注册信息", required = true) @Valid @RequestBody Register register) {
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		register.setLoginName(register.getEmail());
		getLog().info("register= {} ", register.getLoginName());
		return this.loginAuthService.addUser4admin(this.getDomainId(), register);
	}
}