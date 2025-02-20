/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.core.controller;

import java.io.IOException;
import java.util.Map;

import com.wldos.auth.model.AccSecurity;
import com.wldos.auth.vo.AccountInfo;
import com.wldos.framework.controller.RepoController;
import com.wldos.support.resource.vo.DynSet;
import com.wldos.sys.base.vo.Domain;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.core.service.UserService;
import com.wldos.sys.core.vo.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@RequestMapping("user")
@RestController
public class UserController extends RepoController<UserService, WoUser> {

	@GetMapping("route")
	public String checkRoute(@RequestParam String route) {
		return this.resJson.ok(this.service.checkRoute(route, this.getUserId(), this.getDomainId(), this.getTenantId(), this.request));
	}

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