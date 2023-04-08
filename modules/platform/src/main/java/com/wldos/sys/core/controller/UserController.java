/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wldos.auth.model.AccSecurity;
import com.wldos.auth.vo.AccountInfo;
import com.wldos.base.RepoController;
import com.wldos.base.entity.EntityAssists;
import com.wldos.support.resource.vo.DynSet;
import com.wldos.sys.base.vo.Domain;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.core.service.UserService;
import com.wldos.sys.core.vo.Router;
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
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RequestMapping("user")
@RestController
public class UserController extends RepoController<UserService, WoUser> {
	@GetMapping("routes")
	public List<Router> fetchRoutes() {
		String route =
				"[{\"path\":\"/\",\"component\":\"../layouts/BlankLayout\",\"routes\":[{\"path\":\"/user\",\"component\":\"../layouts/UserLayout\",\"routes\":[{\"path\":\"/user/login\",\"component\":\"./user/login\"},{\"path\":\"/user\",\"redirect\":\"/user/login\"},{\"path\":\"/user/register-result\",\"component\":\"./user/register-result\"},{\"path\":\"/user/register\",\"component\":\"./user/register\"},{\"path\":\"/user/active/verify=:verify\",\"component\":\"./user/active\"},{\"path\":\"/user/forget\",\"component\":\"./user/forget\"},{\"component\":\"404\"}]},{\"path\":\"/space\",\"component\":\"../layouts/SecurityLayout\",\"routes\":[{\"path\":\"/space/book\",\"component\":\"./book\"},{\"component\":\"404\"}]},{\"path\":\"/admin\",\"component\":\"../layouts/AdminLayout\",\"routes\":[{\"path\":\"/admin\",\"redirect\":\"/admin/dashboard\"},{\"path\":\"/admin/dashboard\",\"component\":\"./dashboard/monitor\"},{\"path\":\"/admin/sys\",\"routes\":[{\"path\":\"/admin/sys\",\"redirect\":\"/admin/dashboard\"},{\"path\":\"/admin/sys/options\",\"component\":\"./sys/config\"},{\"path\":\"/admin/sys/reg\",\"component\":\"./sys/reg\"},{\"path\":\"/admin/sys/oauth\",\"component\":\"./sys/oauth\"}]},{\"path\":\"/admin/res\",\"routes\":[{\"path\":\"/admin/res/app\",\"component\":\"./sys/app\"},{\"path\":\"/admin/res/res\",\"component\":\"./sys/res\"},{\"path\":\"/admin/res/front\",\"component\":\"./sys/res/frontmenu\"}]},{\"path\":\"/admin/auth\",\"routes\":[{\"path\":\"/admin/auth/role\",\"component\":\"./sys/role\"}]},{\"path\":\"/admin/organ\",\"routes\":[{\"path\":\"/admin/organ/com\",\"component\":\"./sys/com\"},{\"path\":\"/admin/organ/arch\",\"component\":\"./sys/arch\"},{\"path\":\"/admin/organ/org\",\"component\":\"./sys/org\"},{\"path\":\"/admin/organ/user\",\"component\":\"./sys/user\"}]},{\"path\":\"/admin/dom\",\"routes\":[{\"path\":\"/admin/dom/category\",\"component\":\"./sys/category\"},{\"path\":\"/admin/dom/tag\",\"component\":\"./sys/tag\"},{\"path\":\"/admin/dom/domain\",\"component\":\"./sys/domain\"}]},{\"path\":\"/admin/book\",\"routes\":[{\"path\":\"/admin/book/model\",\"component\":\"./sys/category\"},{\"path\":\"/admin/book/lib\",\"component\":\"./sys/category\"},{\"path\":\"/admin/book/sale\",\"component\":\"./sys/category\"},{\"path\":\"/admin/book/audit\",\"component\":\"./sys/category\"}]},{\"path\":\"/admin/info\",\"routes\":[{\"path\":\"/admin/info/class\",\"component\":\"./sys/category\"},{\"path\":\"/admin/info/flow\",\"component\":\"./sys/category\"},{\"path\":\"/admin/info/order\",\"component\":\"./sys/category\"},{\"path\":\"/admin/info/audit\",\"component\":\"./sys/category\"}]},{\"path\":\"/admin/cms\",\"routes\":[{\"path\":\"/admin/cms/pub\",\"routes\":[{\"path\":\"/admin/cms/pub/chapter\",\"component\":\"./sys/article\"},{\"path\":\"/admin/cms/pub/book\",\"component\":\"./sys/book\"},{\"path\":\"/admin/cms/pub/info\",\"component\":\"./sys/info\"}]},{\"path\":\"/admin/cms/comment\",\"component\":\"./sys/comment\"},{\"path\":\"/admin/cms/media\",\"component\":\"./sys/category\"},{\"path\":\"/admin/cms/page\",\"component\":\"./sys/category\"},{\"path\":\"/admin/cms/talk\",\"component\":\"./sys/category\"},{\"path\":\"/admin/cms/appear\",\"component\":\"./sys/category\"},{\"path\":\"/admin/cms/plugin\",\"component\":\"./sys/category\"},{\"path\":\"/admin/cms/set\",\"component\":\"./sys/category\"}]},{\"component\":\"404\"}]},{\"path\":\"/\",\"component\":\"../layouts/BasicLayout\",\"routes\":[{\"path\":\"/index.html\",\"redirect\":\"/\"},{\"path\":\"/index.htm\",\"redirect\":\"/\"},{\"path\":\"/\",\"component\":\"./home\"},{\"path\":\"/product\",\"component\":\"./home/productCategory\"},{\"path\":\"/archives\",\"component\":\"./home/archivesCategory\"},{\"path\":\"/info\",\"component\":\"./home/infoCategory\"},{\"path\":\"/search\",\"component\":\"./search\"},{\"path\":\"/info/pub/create\",\"component\":\"./book/create\"},{\"path\":\"/account\",\"routes\":[{\"path\":\"/\",\"redirect\":\"/account/center\"},{\"path\":\"/account/center\",\"component\":\"./account/center\"},{\"path\":\"/account/settings\",\"component\":\"./account/settings\"}]},{\"component\":\"404\"}]}]}]";
		return this.resJson.readEntity(route, new TypeReference<List<Router>>() {});
	}

	@GetMapping("route")
	public String checkRoute(@RequestParam String route) {
		return this.resJson.ok(this.service.checkRoute(route, this.getCurUserId(), this.getDomainId(), this.getTenantId(), this.request));
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

		return this.service.queryUser(this.getDomainId(), this.request, this.getTenantId(), this.getCurUserId());
	}

	/**
	 * 通过用户访问token获取用户账户设置信息，必须登录
	 *
	 * @return 账户信息
	 */
	@GetMapping("curAccount")
	public AccountInfo currentAccount() {

		return this.service.queryAccountInfo(this.getCurUserId());
	}

	@PostMapping("uploadAvatar")
	public String uploadAvatar(@RequestParam("avatar") MultipartFile file) throws IOException {
		// 头像尺寸一般规定144x144px
		this.service.uploadAvatar(this.request, this.response, file, new int[] { 144, 144 }, this.getCurUserId(), this.getUserIp());

		return this.resJson.ok("ok");
	}

	/**
	 * 用户信息配置,含头像设置
	 *
	 * @param user 基本信息
	 */
	@PostMapping("conf")
	public String userConfig(@RequestBody WoUser user) {
		Long userId = this.getCurUserId();
		EntityAssists.beforeUpdated(user, userId, this.getUserIp());
		user.setId(userId);
		this.service.update(user);

		return this.resJson.ok("ok");
	}

	@PostMapping("conf/tags")
	public String tagsConfig(@RequestBody String tags) {
		this.service.tagsConfig(tags, this.getCurUserId());

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