/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.base.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wldos.support.auth.AuthOpener;
import com.wldos.support.auth.vo.AuthVerify;
import com.wldos.support.cache.ICache;
import com.wldos.support.resource.vo.AuthInfo;
import com.wldos.support.resource.vo.DynSet;
import com.wldos.support.resource.vo.Menu;
import com.wldos.sys.base.repo.DomainResourceRepo;
import com.wldos.sys.base.repo.ResourceRepo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 平台权限体系service。
 *
 * @author 树悉猿
 * @date 2021/4/26
 * @version 1.0
 */
@Slf4j
@Service
public class AuthService implements AuthOpener {
	@Autowired
	@Lazy
	private AuthOpener authOpener;

	private final ResourceRepo resourceRepo;

	private final DomainResourceRepo domainResourceRepo;

	private final TermService termService;

	private final ICache cache;

	@Autowired
	public AuthService(ICache cache, ResourceRepo resourceRepo, DomainResourceRepo domainResourceRepo, TermService termService) {
		this.cache = cache;
		this.resourceRepo = resourceRepo;
		this.domainResourceRepo = domainResourceRepo;
		this.termService = termService;
	}

	/**
	 * 验证用户对请求资源的权限。
	 *
	 * @param domainId 请求的域id
	 * @param appCode 应用编码
	 * @param userId 用户ID为应用程序创建分布式唯一，如果是数据库自动生成，请使用loginName
	 * @param comId 用户主企业id
	 * @param reqUri 请求资源URI
	 * @param reqMethod 请求方法：GET,POST,PUT,DELETE
	 * @return AuthVerify 验证结果
	 */
	public AuthVerify verifyReqAuth(Long domainId, String appCode, Long userId, Long comId, String reqUri, String reqMethod) {
		return this.authOpener.verifyReqAuth(domainId, appCode, userId, comId, reqUri, reqMethod, this.termService, this.resourceRepo, this.cache);
	}

	/**
	 * 查询所有资源权限
	 *
	 * @param appCode 应用编码
	 * @return 应用授权信息
	 */
	public List<AuthInfo> queryAllAuth(String appCode) {
		return this.authOpener.queryAllAuth(appCode, this.resourceRepo, this.cache);
	}

	/**
	 * 刷新某应用资源缓存
	 *
	 * @param appCode 应用编码
	 */
	public void refreshAuth(String appCode) {
		this.authOpener.refreshAuth(appCode, this.cache);
	}

	/**
	 * 查询某域内用户的可见菜单，授权菜单来源：1.组织权限，2.平台权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id，用于判定用户的组织权限
	 * @param menuType 菜单类型：普通菜单、管理菜单
	 * @param id 用户id
	 * @return 菜单列表
	 */
	public List<Menu> queryMenuByUserId(Long domainId, Long comId, String menuType, Long id) {
		return this.authOpener.queryMenuByUserId(domainId, comId, menuType, id, this.resourceRepo);
	}

	/**
	 * 获取页面操作权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id
	 * @param id 用户id
	 * @return 操作权限列表
	 */
	public List<String> queryAuthorityButton(Long domainId, Long comId, Long id) {
		return this.authOpener.queryAuthorityButton(domainId, comId, id, this.resourceRepo);
	}

	/**
	 * 游客专门处理，用于满足互联网应用需求
	 *
	 * @param userId 用户id
	 * @return 是否游客
	 */
	public boolean isGuest(Long userId) {
		return this.authOpener.isGuest(userId);
	}

	/**
	 * 查询动态路由配置
	 *
	 * @param domainId 域名id
	 * @return 动态路由配置
	 */
	public Map<String, DynSet> queryDynRoute(Long domainId) {
		return this.authOpener.queryDynRoute(this.termService, this.domainResourceRepo, domainId);
	}

	/**
	 *
	 * 如果是放开路由 直接返回200
	 * 如果是鉴权路由 先检查token，游客返回401
	 * 已登录检查是否有权限，无权限返回403，否则返回200
	 */
	public String authorityRouteCheck(String route, Long userId, Long domainId, Long tenantId, HttpServletRequest request) {
		return this.authOpener.authorityRoute(route, userId, domainId, tenantId, request, this);
	}
}