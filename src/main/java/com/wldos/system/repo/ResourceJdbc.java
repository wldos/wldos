/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.repo;

import java.util.List;

import com.wldos.system.vo.AuthInfo;
import com.wldos.system.entity.WoResource;

/**
 * Paas平台资源复杂查询接口声明。
 *
 * @Title ResourceJdbc
 * @Package com.wldos.system.repo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/27
 * @Version 1.0
 */
public interface ResourceJdbc {
	/**
	 * 查询SaaS平台某个用户在某应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param appCode 应用编码
	 * @param userId 用户ID
	 * @return
	 */
	List<AuthInfo> queryAuthInfo(String appCode, Long userId);

	/**
	 * 查询SaaS平台某个应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param appCode 应用编码
	 * @return
	 */
	List<AuthInfo> queryAuthInfo(String appCode);

	/**
	 * 根据资源类型、用户ID查找用户有操作权限的某类资源。
	 *
	 * @param type
	 * @param userId
	 * @return
	 */
	List<WoResource> queryResource(String type, Long userId);

	/**
	 * 查询SaaS平台某个角色自有资源。
	 *
	 * @param roleId 角色ID
	 * @return
	 */
	List<WoResource> queryResourceByRoleId(Long roleId);

	/**
	 * 查询SaaS平台某个角色继承父角色的资源。
	 *
	 * @param roleId 角色ID
	 * @return
	 */
	List<WoResource> queryResourceByInheritRoleId(Long roleId);

	/**
	 * 查询游客在某应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param appCode 应用编码
	 * @return
	 */
	List<AuthInfo> queryAuthInfoForGuest(String appCode);

	/**
	 * 根据资源类型查询游客有操作权限的某类资源。
	 *
	 * @param type
	 * @return
	 */
	List<WoResource> queryResourceForGuest(String type);
}
