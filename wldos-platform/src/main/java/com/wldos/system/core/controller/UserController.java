/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.io.IOException;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.controller.RepoController;
import com.wldos.system.auth.model.AccSecurity;
import com.wldos.system.auth.vo.AccountInfo;
import com.wldos.system.storage.vo.FileInfo;
import com.wldos.system.core.entity.WoUser;
import com.wldos.system.core.service.UserService;
import com.wldos.system.vo.Domain;
import com.wldos.system.vo.User;

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
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RequestMapping("user")
@RestController
public class UserController extends RepoController<UserService, WoUser> {
	
	@GetMapping("currentUser")
	public User currentUser() {

		return this.service.queryUser(this.getDomain(), this.request, this.getTenantId(), this.getCurUserId());
	}
	
	@GetMapping("curAccount")
	public AccountInfo currentAccount() {

		return this.service.queryAccountInfo(this.getCurUserId());
	}

	@PostMapping("uploadAvatar")
	public String uploadAvatar(@RequestParam("avatar") MultipartFile file) throws IOException {

		FileInfo fileInfo = this.store.storeFile(this.request, this.response, file, new int[]{144, 144});

		WoUser user = new WoUser();
		Long userId = this.getCurUserId();
		user.setId(userId);
		user.setAvatar(fileInfo.getPath());
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		this.service.update(user);

		return this.resJson.ok("ok");
	}
	
	@PostMapping("conf")
	public String userConfig(@RequestBody WoUser user) {
		Long userId = this.getCurUserId();
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		user.setId(userId);
		this.service.update(user);

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/sec")
	public String securityConfig(@RequestBody AccSecurity sec) {
		this.service.accountConfig(sec, this.getCurUserId());

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/bind")
	public String bindConfig(@RequestBody WoUser user) {
		Long userId = this.getCurUserId();
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		user.setId(userId);
		this.service.update(user);

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/notice")
	public String noticeConfig(@RequestBody WoUser user) {
		Long userId = this.getCurUserId();
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		user.setId(userId);
		this.service.update(user);

		return this.resJson.ok("ok");
	}
	
	@GetMapping("curDomain")
	public Domain curDomain() {

		return this.service.findByDomain(this.getDomain());
	}
	
	@GetMapping("slogan")
	public String curDomainSlogan() {
		String slogan = this.service.querySloganByDomain(this.getDomain());

		return this.resJson.ok("slogan", slogan);
	}
}
