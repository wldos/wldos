/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.support.resource.ResourceOpener;
import com.wldos.support.resource.entity.WoResource;
import com.wldos.support.resource.vo.AuthInfo;

import org.springframework.transaction.annotation.Transactional;

/**
 * 平台资源复杂查询接口声明。
 *
 * @author 元悉宇宙
 * @date 2021/4/27
 * @version 1.0
 */
@SuppressWarnings("unused")
@Transactional(readOnly = true) // 接口层面开启只读事务，防止死锁，写方法直接用@Transactional注解覆盖只读属性
public interface ResourceJdbc extends ResourceOpener {
	/**
	 * 查询SaaS平台某个用户在某应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param domainId 域id
	 * @param comId 公司id
	 * @param appCode 应用编码
	 * @param userId 用户ID
	 * @return 授权信息
	 */
	List<AuthInfo> queryAuthInfo(Long domainId, Long comId, String appCode, Long userId);

	/**
	 * 查询SaaS平台某个应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param appCode 应用编码
	 * @return 应用授权信息
	 */
	List<AuthInfo> queryAuthInfo(String appCode);

	/**
	 * 根据资源类型、用户ID查找用户有操作权限的某类资源。
	 *
	 * @param domainId 域名id
	 * @param comId 公司id
	 * @param type 资源类型
	 * @param userId 用户id
	 * @return 可见资源列表
	 */
	List<WoResource> queryResource(Long domainId, Long comId, String type, Long userId);

	/**
	 * 查询SaaS平台某个角色自有资源。
	 *
	 * @param roleId 角色ID
	 * @return 角色授权信息
	 */
	List<WoResource> queryResourceByRoleId(Long roleId);

	/**
	 * 查询SaaS平台某个角色继承父角色的资源。
	 *
	 * @param roleId 角色ID
	 * @return 角色继承资源
	 */
	List<WoResource> queryResourceByInheritRoleId(Long roleId);

	/**
	 * 查询游客在某应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param domainId 域id
	 * @param appCode 应用编码
	 * @return 游客授权信息
	 */
	List<AuthInfo> queryAuthInfoForGuest(Long domainId, String appCode);

	/**
	 * 根据资源类型查询游客有操作权限的某类资源。
	 *
	 * @param domainId 域名id
	 * @param type 资源类型
	 * @return 游客资源
	 */
	List<WoResource> queryResourceForGuest(Long domainId, String type);
}
