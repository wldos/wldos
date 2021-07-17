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
import com.wldos.system.auth.vo.AccountInfo;
import com.wldos.system.storage.file.vo.FileInfo;
import com.wldos.system.core.entity.WoUser;
import com.wldos.system.core.service.UserService;
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


	/**
	 * 通过用户访问token获取用户信息、权限和菜单
	 *
	 * @return
	 */
	@GetMapping("currentUser")
	public String currentUser() {
		User user = this.service.queryUser(this.getCurUserId(), this.getToken());

		return this.resJson.ok(user);
	}

	/**
	 * 通过用户访问token获取用户账户设置信息，必须登录
	 *
	 * @return
	 */
	@GetMapping("curAccount")
	public String currentAccount() {
		AccountInfo accInfo = this.service.queryAccountInfo(this.getCurUserId());

		return this.resJson.ok(accInfo);
	}

	@PostMapping("uploadAvatar")
	public String uploadAvatar(@RequestParam("avatar") MultipartFile file) throws IOException {
		// 调用文件存储服务, 头像尺寸规定144x144px
		FileInfo fileInfo = this.store.storeFile(this.request, this.response, file, new int[]{144, 144});

		WoUser user = new WoUser();
		Long userId = this.getCurUserId();
		user.setId(userId);
		user.setAvatar(fileInfo.getPath());
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		this.service.update(user);

		return this.resJson.ok("ok");
	}

	/**
	 * 用户信息配置,含头像设置
	 * 考虑到用户头像在SaaS平台量较大，与文件服务合并统一处理支持独立部署，支持返回fileId或者相对Url，消费方可以存储id或者URL
	 * 存储URL可以节省时间，存储空间略有冗余，建议在外网有seo需求的应用中使用，在内网附件更新频繁的业务系统，建议用ID。
	 *
	 * @param user 基本信息
	 * @return
	 */
	@PostMapping("conf")
	public String userConfig(@RequestBody WoUser user) {
		Long userId = this.getCurUserId();
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		user.setId(userId);
		this.service.update(user);

		return this.resJson.ok("ok");
	}
}
