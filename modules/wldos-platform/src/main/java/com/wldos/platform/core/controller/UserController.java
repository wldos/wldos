/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.wldos.platform.auth.model.AccSecurity;
import com.wldos.platform.auth.vo.AccountInfo;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.vo.Domain;
import com.wldos.platform.core.vo.UserAuth;
import com.wldos.platform.support.resource.vo.DynSet;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.service.UserService;
import com.wldos.platform.core.vo.User;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 用户相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@RequestMapping("user")
@RestController
public class UserController extends EntityController<UserService, WoUser> {



	/** 路由守护，检查路由是否可访问 */
	@GetMapping("route")
	public String checkRoute(@RequestParam String route) {
		return this.resJson.ok(this.service.checkRoute(route, this.getUserId(), this.getDomainId(), this.getTenantId(), this.request));
	}

	/**
	 * 获取首页以及其他多模板页面的动态模板配置
	 * 
	 * @return 动态模板配置
	 */
	@GetMapping("dynamite")
	public Map<String, DynSet> fetchDynRoute() {
		return this.service.queryDynRoute(this.getDomainId());
	}

	/**
	 * 通过用户访问token获取用户信息、权限和菜单
	 *
	 * @return 用户信息
	 */
	@GetMapping("currentUser")
	public User currentUser() {

		return this.service.queryUser(this.getDomainId(), this.request, this.getTenantId(), this.getUserId());
	}

	/**
	 * 通过指定用户访问token获取用户信息、权限和菜单
	 *
	 * @return 用户信息
	 */
	@GetMapping("menu/{userId}")
	public UserAuth fetchUserAuth(@PathVariable Long userId) {

		return this.service.queryUserAuth(this.getDomainId(), this.request, this.getTenantId(), userId);
	}

	/**
	 * 通过用户访问token获取用户账户设置信息，必须登录
	 *
	 * @return 账户信息
	 */
	@GetMapping("curAccount")
	public AccountInfo currentAccount() {

		return this.service.queryAccountInfo(this.getUserId());
	}

	@PostMapping("uploadAvatar")
	public String uploadAvatar(@RequestParam("avatar") MultipartFile file) throws IOException {
		// 头像尺寸一般规定144x144px
		this.service.uploadAvatar(this.request, this.response, file, new int[] { 144, 144 }, this.getUserId(), this.getUserIp());

		return this.resJson.ok("ok");
	}

	/**
	 * 用户信息配置,含头像设置
	 *
	 * @param user 基本信息
	 */
	@PostMapping("conf")
	public String userConfig(@RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.update(user, true);

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/tags")
	public String tagsConfig(@RequestBody String tags) {
		this.service.tagsConfig(tags, this.getUserId());

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/sec")
	public String securityConfig(@RequestBody AccSecurity sec) {
		this.service.accountConfig(sec, this.getUserId());

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/bind")
	public String bindConfig(@RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.update(user, true);

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/notice")
	public String noticeConfig(@RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.update(user, true);

		return this.resJson.ok("ok");
	}

	/**
	 * 取回请求域的seo信息
	 *
	 * @return 请求域的信息
	 */
	@GetMapping("curDomain")
	public Domain curDomain() {

		return this.service.findByDomain(this.getDomain());
	}

	/**
	 * 取回请求域的slogan
	 *
	 * @return 请求域的slogan信息
	 */
	@GetMapping("slogan")
	public String curDomainSlogan() {
		String slogan = this.service.querySloganByDomain(this.getDomain());

		return this.resJson.ok("slogan", slogan);
	}


}