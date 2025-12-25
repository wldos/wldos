/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wldos.platform.support.auth.vo.AuthVerify;
import com.wldos.platform.support.resource.vo.AuthInfo;
import com.wldos.platform.support.resource.vo.DynSet;
import com.wldos.platform.support.resource.vo.Menu;
import com.wldos.platform.support.term.TermOpener;
import com.wldos.framework.support.cache.ICache;
import com.wldos.platform.support.domain.DomainResourceOpener;
import com.wldos.platform.support.resource.ResourceOpener;

/**
 * 平台权限体系开瓶器。
 *
 * @author 元悉宇宙
 * @date 2021/4/26
 * @version 1.0
 */
public interface AuthOpener {

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
	default AuthVerify verifyReqAuth(Long domainId, String appCode, Long userId, Long comId, String reqUri, String reqMethod,
                                     TermOpener termOpener, ResourceOpener resourceRepo, ICache cache) {return null;}

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
	default AuthVerify verifyReqAuth(Long domainId, String appCode, Long userId, Long comId, String reqUri, String reqMethod) { return null;}

	/**
	 * 查询所有资源权限
	 *
	 * @param appCode 应用编码
	 * @return 应用授权信息
	 */
	default List<AuthInfo> queryAllAuth(String appCode, ResourceOpener resourceRepo, ICache cache) {return null;}

	/**
	 * 刷新某应用资源缓存
	 *
	 * @param appCode 应用编码
	 */
	default void refreshAuth(String appCode, ICache cache) {}

	/**
	 * 查询某域内用户的可见菜单，授权菜单来源：1.组织权限，2.平台权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id，用于判定用户的组织权限
	 * @param menuType 菜单类型：普通菜单、管理菜单
	 * @param id 用户id
	 * @return 菜单列表
	 */
	default List<Menu> queryMenuByUserId(Long domainId, Long comId, String menuType, Long id, ResourceOpener resourceRep) {return null;}

	/**
	 * 查询某域内用户的可见菜单，授权菜单来源：1.组织权限，2.平台权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id，用于判定用户的组织权限
	 * @param menuTypes 菜单类型：普通菜单(含插件)、管理菜单（含插件）
	 * @param id 用户id
	 * @return 菜单列表
	 */
	default List<Menu> queryMenuByUserId(Long domainId, Long comId, String[] menuTypes, Long id, ResourceOpener resourceRep) {return null;}

	/**
	 * 获取页面操作权限。
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id
	 * @param id 用户id
	 * @return 操作权限列表
	 */
	default List<String> queryAuthorityButton(Long domainId, Long comId, Long id, ResourceOpener resourceRepo) {return null;}

	/**
	 * 游客专门处理，用于满足互联网应用需求
	 *
	 * @param userId 用户id
	 * @return 是否游客
	 */
	boolean isGuest(Long userId);

	/**
	 * 查询动态路由配置
	 *
	 * @param domainId 域名id
	 * @return 动态路由配置
	 */
	default Map<String, DynSet> queryDynRoute(TermOpener termOpener, DomainResourceOpener domainResourceRepo, Long domainId) {return null;}

	default String authorityRoute(String route, Long userId, Long domainId, Long tenantId, HttpServletRequest request,
			AuthOpener authService) {return null;}
}