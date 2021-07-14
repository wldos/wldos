/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.service;

import java.util.List;

import com.wldos.support.service.BaseService;
import com.wldos.system.repo.ResourceRepo;
import com.wldos.system.vo.AuthInfo;
import com.wldos.system.entity.WoResource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源操作service。
 *
 * @Title WoResourceService
 * @Package com.wldos.system.service
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/28
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceService extends BaseService<ResourceRepo, WoResource, Long> {
	/**
	 * 查询指定应用下某用户的授权信息
	 *
	 * @param appCode
	 * @param userId
	 * @return
	 */
	public List<AuthInfo> queryAuthInfo(String appCode, Long userId) {
		return this.entityRepo.queryAuthInfo(appCode, userId);
	}

	public List<AuthInfo> queryAuthInfo(String appCode) {
		return this.entityRepo.queryAuthInfo(appCode);
	}

	public List<WoResource> queryResource(String type) {
		return null;
	}

	public List<WoResource> queryResourceByRoleId(Long roleId) {
		return this.entityRepo.queryResourceByRoleId(roleId);
	}

	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		return this.entityRepo.queryResourceByInheritRoleId(roleId);
	}

	/**
	 * 查询指定用户、类型的授权资源
	 *
	 * @param type
	 * @param userId
	 * @return
	 */
	public List<WoResource> queryResource(String type, Long userId) {
		return this.entityRepo.queryResource(type, userId);
	}

	/**
	 * 查询游客在某应用(模块)下的所有资源，并拼装成权限信息。
	 *
	 * @param appCode 应用编码
	 * @return
	 */
	public List<AuthInfo> queryAuthInfoForGuest(String appCode) {
		return this.entityRepo.queryAuthInfoForGuest(appCode);
	}

	/**
	 * 根据资源类型查询游客有操作权限的某类资源。
	 *
	 * @param type
	 * @return
	 */
	public List<WoResource> queryResourceForGuest(String type) {
		return this.entityRepo.queryResourceForGuest(type);
	}
}
