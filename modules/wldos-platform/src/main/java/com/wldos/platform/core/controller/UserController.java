/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.io.IOException;
import java.util.Map;

import com.wldos.platform.auth.model.AccSecurity;
import com.wldos.platform.auth.vo.AccountInfo;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.vo.Domain;
import com.wldos.platform.core.vo.UserAuth;
import com.wldos.platform.support.resource.vo.DynSet;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.service.UserService;
import com.wldos.platform.core.vo.User;
import com.wldos.common.res.Result;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 用户相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "用户管理")
@RequestMapping("user")
@RestController
public class UserController extends EntityController<UserService, WoUser> {



	/** 路由守护，检查路由是否可访问 */
	@ApiOperation(value = "路由检查", notes = "检查路由是否可访问")
	@GetMapping("route")
	public Result checkRoute(@ApiParam(value = "路由路径", required = true) @RequestParam String route) {
		return Result.ok(this.service.checkRoute(route, this.getUserId(), this.getDomainId(), this.getTenantId(), this.request));
	}

	/**
	 * 获取首页以及其他多模板页面的动态模板配置
	 * 
	 * @return 动态模板配置
	 */
	@ApiOperation(value = "动态模板配置", notes = "获取首页以及其他多模板页面的动态模板配置")
	@GetMapping("dynamite")
	public Map<String, DynSet> fetchDynRoute() {
		return this.service.queryDynRoute(this.getDomainId());
	}

	/**
	 * 通过用户访问token获取用户信息、权限和菜单
	 *
	 * @return 用户信息
	 */
	@ApiOperation(value = "当前用户信息", notes = "通过用户访问token获取用户信息、权限和菜单")
	@GetMapping("currentUser")
	public User currentUser() {

		return this.service.queryUser(this.getDomainId(), this.request, this.getTenantId(), this.getUserId());
	}

	/**
	 * 通过指定用户访问token获取用户信息、权限和菜单
	 *
	 * @return 用户信息
	 */
	@ApiOperation(value = "用户权限信息", notes = "通过指定用户访问token获取用户信息、权限和菜单")
	@GetMapping("menu/{userId}")
	public UserAuth fetchUserAuth(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {

		return this.service.queryUserAuth(this.getDomainId(), this.request, this.getTenantId(), userId);
	}

	/**
	 * 通过用户访问token获取用户账户设置信息，必须登录
	 *
	 * @return 账户信息
	 */
	@ApiOperation(value = "当前账户信息", notes = "通过用户访问token获取用户账户设置信息")
	@GetMapping("curAccount")
	public AccountInfo currentAccount() {

		return this.service.queryAccountInfo(this.getUserId());
	}

	@ApiOperation(value = "上传头像", notes = "上传用户头像")
	@PostMapping("uploadAvatar")
	public Result uploadAvatar(@ApiParam(value = "头像文件", required = true) @RequestParam("avatar") MultipartFile file) throws IOException {
		// 头像尺寸一般规定144x144px
		this.service.uploadAvatar(this.request, this.response, file, new int[] { 144, 144 }, this.getUserId(), this.getUserIp());

		return Result.ok("ok");
	}

	/**
	 * 用户信息配置,含头像设置
	 *
	 * @param user 基本信息
	 */
	@ApiOperation(value = "用户信息配置", notes = "用户信息配置,含头像设置")
	@PostMapping("conf")
	public Result userConfig(@ApiParam(value = "用户信息", required = true) @Valid @RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.saveOrUpdate(user);

		return Result.ok("ok");
	}

	@ApiOperation(value = "标签配置", notes = "配置用户标签")
	@PostMapping("conf/tags")
	public Result tagsConfig(@ApiParam(value = "标签JSON", required = true) @RequestBody String tags) {
		this.service.tagsConfig(tags, this.getUserId());

		return Result.ok("ok");
	}

	@ApiOperation(value = "安全配置", notes = "配置用户安全设置")
	@PostMapping("conf/sec")
	public Result securityConfig(@ApiParam(value = "安全配置", required = true) @Valid @RequestBody AccSecurity sec) {
		this.service.accountConfig(sec, this.getUserId());

		return Result.ok("ok");
	}

	@ApiOperation(value = "绑定配置", notes = "配置用户绑定信息")
	@PostMapping("conf/bind")
	public Result bindConfig(@ApiParam(value = "用户信息", required = true) @Valid @RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.saveOrUpdate(user);

		return Result.ok("ok");
	}

	@ApiOperation(value = "通知配置", notes = "配置用户通知设置")
	@PostMapping("conf/notice")
	public Result noticeConfig(@ApiParam(value = "用户信息", required = true) @Valid @RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.saveOrUpdate(user);

		return Result.ok("ok");
	}

	/**
	 * 取回请求域的seo信息
	 *
	 * @return 请求域的信息
	 */
	@ApiOperation(value = "当前域名信息", notes = "取回请求域的seo信息")
	@GetMapping("curDomain")
	public Domain curDomain() {

		return this.service.findByDomain(this.getDomain());
	}

	/**
	 * 取回请求域的slogan
	 *
	 * @return 请求域的slogan信息
	 */
	@ApiOperation(value = "域名Slogan", notes = "取回请求域的slogan")
	@GetMapping("slogan")
	public String curDomainSlogan() {
		String slogan = this.service.querySloganByDomain(this.getDomain());

		return this.resJson.ok("slogan", slogan);
	}


}