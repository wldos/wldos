/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.wldos.platform.core.service.OptionsService;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.framework.support.notice.UserNoticesAggregator;
import com.wldos.platform.audit.service.LoginLogService;
import com.wldos.platform.audit.service.OpLogService;
import com.wldos.platform.audit.vo.LoginLogItem;
import com.wldos.platform.audit.vo.OpLogItem;
import com.wldos.platform.core.vo.AccountCenterTabDef;
import com.wldos.platform.auth.model.AccSecurity;
import com.wldos.platform.auth.vo.AccountInfo;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.vo.Domain;
import com.wldos.platform.core.vo.UserAuth;
import io.github.wldos.platform.support.resource.vo.DynSet;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.service.UserService;
import com.wldos.platform.core.service.AccountCenterTabAccessService;
import com.wldos.platform.core.service.UserPortalShortcutService;
import com.wldos.platform.core.vo.MyShortcutCreateBody;
import com.wldos.platform.core.vo.MyShortcutTouchBody;
import com.wldos.platform.core.vo.MyShortcutUpdateBody;
import com.wldos.platform.core.vo.PortalShortcutVo;
import com.wldos.platform.core.vo.User;
import io.github.wldos.common.res.Result;
import io.github.wldos.framework.support.audit.annotation.OpLog;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 用户相关controller。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "用户管理")
@RequestMapping("user")
@RestController
public class UserController extends EntityController<UserService, WoUser> {


	@Autowired
	private OptionsService optionsService;

	@Autowired
	private AccountCenterTabAccessService accountCenterTabAccessService;

	@Autowired
	private LoginLogService loginLogService;

	@Autowired
	private OpLogService opLogService;

	@Autowired
	private UserPortalShortcutService userPortalShortcutService;

	@Autowired(required = false)
	private ObjectProvider<UserNoticesAggregator> userNoticesAggregatorProvider;

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
	@ApiOperation(value = "当前用户信息", notes = "通过 token 返回用户、门户菜单与权限。User.isManageSide 当前由 DTO 默认，前端按需区分用户端/管理端语境，不在此接口中计算")
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

	/**
	 * 校验推荐码是否有效（下单前调用，可选登录）
	 * 若已登录且推荐码为当前用户自己的码，返回 valid=false
	 */
	@ApiOperation(value = "推荐码校验", notes = "校验推荐码是否有效，用于结算页")
	@GetMapping("referral/validate")
	public Result<Map<String, Object>> validateReferralCode(@ApiParam("推荐码") @RequestParam String code) {
		Optional<WoUser> opt = this.service.findByRecommendCode(code);
		Map<String, Object> data = new HashMap<>();
		if (!opt.isPresent()) {
			data.put("valid", false);
			data.put("message", "推荐码无效或已失效");
			return Result.ok(data);
		}
		WoUser referrer = opt.get();
		Long currentUserId = this.getUserId();
		if (currentUserId != null && currentUserId.equals(referrer.getId())) {
			data.put("valid", false);
			data.put("message", "不能使用自己的推荐码");
			return Result.ok(data);
		}
		data.put("valid", true);
		data.put("recommendCode", referrer.getRecommendCode());
		data.put("nickname", referrer.getNickname());
		data.put("referrerUserId", referrer.getId());
		// 优惠金额由产品/规则计算，此处仅占位；前端或结算页可按规则计算后传 discountAmount 下单
		data.put("discountAmount", 0);
		return Result.ok(data);
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
	@OpLog(action = "更新个人资料", resourceType = "user_profile", resourceId = "#user.id")
	public Result userConfig(@ApiParam(value = "用户信息", required = true) @Valid @RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.saveOrUpdate(user);

		return Result.ok("ok");
	}

	@ApiOperation(value = "标签配置", notes = "配置用户标签")
	@PostMapping("conf/tags")
	@OpLog(action = "更新个人标签", resourceType = "user_tags")
	public Result tagsConfig(@ApiParam(value = "标签JSON", required = true) @RequestBody String tags) {
		this.service.tagsConfig(tags, this.getUserId());

		return Result.ok("ok");
	}

	@ApiOperation(value = "安全配置", notes = "配置用户安全设置")
	@PostMapping("conf/sec")
	@OpLog(action = "更新账户安全设置", resourceType = "user_security",
			sensitiveFields = {"oldPassword", "newPassword", "passwd", "password", "pwd", "code", "captcha"})
	public Result securityConfig(@ApiParam(value = "安全配置", required = true) @Valid @RequestBody AccSecurity sec) {
		this.service.accountConfig(sec, this.getUserId());

		return Result.ok("ok");
	}

	@ApiOperation(value = "绑定配置", notes = "配置用户绑定信息")
	@PostMapping("conf/bind")
	@OpLog(action = "更新账户绑定", resourceType = "user_bind", resourceId = "#user.id",
			sensitiveFields = {"password", "passwd", "pwd", "code", "captcha"})
	public Result bindConfig(@ApiParam(value = "用户信息", required = true) @Valid @RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.saveOrUpdate(user);

		return Result.ok("ok");
	}

	@ApiOperation(value = "通知配置", notes = "配置用户通知设置")
	@PostMapping("conf/notice")
	@OpLog(action = "更新通知设置", resourceType = "user_notice", resourceId = "#user.id")
	public Result noticeConfig(@ApiParam(value = "用户信息", required = true) @Valid @RequestBody WoUser user) {
		Long userId = this.getUserId();
		user.setId(userId);
		this.service.saveOrUpdate(user);

		return Result.ok("ok");
	}

	/** 通知列表短时缓存：TTL 45s，与前端 60s 轮询错峰，减轻重复聚合/查库 */
	private static final long NOTICES_CACHE_TTL_MS = 45_000L;
	private static final ConcurrentHashMap<Long, Object[]> NOTICES_CACHE = new ConcurrentHashMap<>(64);

	private List<Map<String, Object>> getNoticesList(Long userId) {
		if (userId == null) {
			return Collections.emptyList();
		}
		long now = System.currentTimeMillis();
		Object[] entry = NOTICES_CACHE.get(userId);
		if (entry != null && (Long) entry[0] > now) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> cached = (List<Map<String, Object>>) entry[1];
			return cached;
		}
		UserNoticesAggregator aggregator = userNoticesAggregatorProvider.getIfAvailable();
		List<Map<String, Object>> notices = aggregator == null
			? Collections.emptyList()
			: aggregator.aggregate(userId, 80);
		NOTICES_CACHE.put(userId, new Object[] { now + NOTICES_CACHE_TTL_MS, notices });
		return notices;		
	}

	/**
	 * 统一通知列表（消息气泡：工单提醒、站内信、邮件等）
	 */
	@ApiOperation(value = "通知列表", notes = "消息气泡数据源，点击气泡时拉取")
	@GetMapping("notices")
	public Result<List<Map<String, Object>>> notices() {
		return Result.ok(getNoticesList(this.getUserId()));
	}

	/**
	 * 轻量检查：仅返回条数，供前端轮询是否有新消息、更新角标；不返回列表以减小响应。
	 */
	@ApiOperation(value = "通知条数", notes = "轮询用，仅返回 totalCount/unreadCount，有新增时前端再调 notices 拉列表")
	@GetMapping("notices/count")
	public Result<Map<String, Integer>> noticesCount() {
		List<Map<String, Object>> list = getNoticesList(this.getUserId());
		int total = list.size();
		int unread = (int) list.stream().filter(m -> !Boolean.TRUE.equals(m.get("read"))).count();
		Map<String, Integer> count = new HashMap<>(2);
		count.put("totalCount", total);
		count.put("unreadCount", unread);
		return Result.ok(count);
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

	/**
	 * 门户顶栏搜索下拉提示（读系统配置 portal_search_hints，支持 JSON 数组或换行分隔）。
	 */
	@ApiOperation(value = "门户搜索提示词", notes = "系统配置 portal_search_hints，JSON 字符串数组或每行一条")
	@GetMapping("searchHints")
	public Result<List<String>> portalSearchHints() {
		return Result.ok(this.optionsService.findPortalSearchHints());
	}

	/**
	 * 个人中心页签定义（系统配置 portal_account_center_tabs）；空列表时前端使用内置 CMS 默认页签。
	 */
	@ApiOperation(value = "个人中心页签", notes = "仅登录会员；游客无账号。优先域级 portal_account_center_tabs；列表按角色/资源/管理端能力裁剪")
	@GetMapping("accountCenterTabDefs")
	public Result<List<AccountCenterTabDef>> accountCenterTabDefs() {
		if (this.service.isGuestUser(this.getUserId())) {
			return Result.ok(Collections.emptyList());
		}
		List<AccountCenterTabDef> configured = this.optionsService.findPortalAccountCenterTabs(this.getDomainId());
		if (configured.isEmpty()) {
			return Result.ok(Collections.emptyList());
		}
		return Result.ok(this.accountCenterTabAccessService.filterVisibleForCurrentUser(this.getUserId(), this.getDomainId(),
				this.getTenantId(), this.request, configured));
	}

	/**
	 * 当前登录用户自己的登录日志分页；忽略请求中的 userId，非超级管理员按租户+域隔离。
	 */
	@ApiOperation(value = "我的登录日志", notes = "仅本人；分页参数同管理端登录日志")
	@GetMapping("myLoginLogs")
	public PageData<LoginLogItem> myLoginLogs(@RequestParam Map<String, Object> params) {
		Long uid = this.getUserId();
		if (uid == null) {
			return new PageData<>(0L, 1, 10, new ArrayList<>(0));
		}
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.removeParam("userId");
		pageQuery.pushParam("userId", uid);
		if (!this.isAdmin(uid)) {
			this.applyTenantFilter(pageQuery);
			this.applyDomainFilter(pageQuery);
		}
		return this.loginLogService.queryList(pageQuery);
	}

	/**
	 * 当前登录用户自己的操作日志分页；忽略请求中的 userId，非超级管理员按租户+域隔离。
	 */
	@ApiOperation(value = "我的操作记录", notes = "仅本人；分页参数同管理端操作日志")
	@GetMapping("myOpLogs")
	public PageData<OpLogItem> myOpLogs(@RequestParam Map<String, Object> params) {
		Long uid = this.getUserId();
		if (uid == null) {
			return new PageData<>(0L, 1, 10, new ArrayList<>(0));
		}
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.removeParam("userId");
		pageQuery.pushParam("userId", uid);
		if (!this.isAdmin(uid)) {
			this.applyTenantFilter(pageQuery);
			this.applyDomainFilter(pageQuery);
		}
		return this.opLogService.queryList(pageQuery);
	}

	/**
	 * 门户个人中心「我的常用」列表（当前用户 + 租户 + 域，按 display_order）。
	 */
	@ApiOperation(value = "我的常用列表", notes = "仅本人；服务端持久化，多终端一致")
	@GetMapping("myShortcuts")
	public Result<List<PortalShortcutVo>> myShortcuts() {
		Long uid = this.getUserId();
		Long comId = this.getTenantId();
		Long domId = this.getDomainId();
		if (uid == null || comId == null || domId == null) {
			return Result.ok(Collections.emptyList());
		}
		return Result.ok(this.userPortalShortcutService.listVo(uid, comId, domId));
	}

	@ApiOperation(value = "新增我的常用", notes = "path 须为站内路径；服务端按 GET 校验路由可访问性")
	@PostMapping("myShortcuts")
	public Result<PortalShortcutVo> myShortcutsAdd(@Valid @RequestBody MyShortcutCreateBody body) {
		return this.userPortalShortcutService.addMine(body, this.getUserId(), this.getTenantId(), this.getDomainId(), this.request);
	}

	@ApiOperation(value = "我的常用访问打点", notes = "浏览门户菜单时调用，累加次数并参与「我的常用」排序（未置顶按频次倒序）")
	@PostMapping("myShortcuts/touch")
	public Result<PortalShortcutVo> myShortcutsTouch(@Valid @RequestBody MyShortcutTouchBody body) {
		return this.userPortalShortcutService.touchMine(body, this.getUserId(), this.getTenantId(), this.getDomainId(),
				this.request);
	}

	@ApiOperation(value = "更新我的常用")
	@PutMapping("myShortcuts/{id}")
	public Result<PortalShortcutVo> myShortcutsUpdate(@PathVariable long id, @RequestBody MyShortcutUpdateBody body) {
		return this.userPortalShortcutService.updateMine(id, body, this.getUserId(), this.getTenantId(), this.getDomainId(),
				this.request);
	}

	@ApiOperation(value = "删除我的常用")
	@DeleteMapping("myShortcuts/{id}")
	public Result<Void> myShortcutsDelete(@PathVariable long id) {
		return this.userPortalShortcutService.deleteMine(id, this.getUserId(), this.getTenantId(), this.getDomainId());
	}


}