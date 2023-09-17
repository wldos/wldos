/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.support.resource.ResourceOpener;
import com.wldos.support.resource.entity.WoResource;
import com.wldos.support.resource.vo.AuthInfo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 平台资源复杂查询实现类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
@Slf4j
public class ResourceJdbcImpl implements ResourceJdbc {
	@Autowired
	@Lazy
	private ResourceOpener resourceOpener;

	@Override
	public List<AuthInfo> queryAuthInfo(Long domainId, Long comId, String appCode, Long userId) {

		return this.resourceOpener.queryAuthInfo(domainId, comId, appCode, userId);
	}

	@Override
	public List<AuthInfo> queryAuthInfo(String appCode) {
		return this.resourceOpener.queryAuthInfo(appCode);
	}

	@Override
	public List<WoResource> queryResource(Long domainId, Long comId, String type, Long userId) {
		return this.resourceOpener.queryResource(domainId, comId, type, userId);
	}

	@Override
	public List<WoResource> queryResourceByRoleId(Long roleId) {
		return this.resourceOpener.queryResourceByRoleId(roleId);
	}

	@Override
	public List<WoResource> queryResourceByInheritRoleId(Long roleId) {
		return this.resourceOpener.queryResourceByInheritRoleId(roleId);
	}

	@Override
	public List<AuthInfo> queryAuthInfoForGuest(Long domainId, String appCode) {
		return this.resourceOpener.queryAuthInfoForGuest(domainId, appCode);
	}

	@Override
	public List<WoResource> queryResourceForGuest(Long domainId, String type) {
		return this.resourceOpener.queryResourceForGuest(domainId, type);
	}
}