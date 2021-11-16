/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.repo;

import java.util.List;

import com.wldos.system.vo.AuthInfo;
import com.wldos.system.core.entity.WoResource;

/**
 * 平台资源复杂查询接口声明。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
@SuppressWarnings("unused")
public interface ResourceJdbc {

	List<AuthInfo> queryAuthInfo(Long domainId, Long comId, String appCode, Long userId);

	List<AuthInfo> queryAuthInfo(String appCode);

	List<WoResource> queryResource(Long domainId, Long comId, String type, Long userId);

	List<WoResource> queryResourceByRoleId(Long roleId);

	List<WoResource> queryResourceByInheritRoleId(Long roleId);

	List<AuthInfo> queryAuthInfoForGuest(Long domainId, String appCode);

	List<WoResource> queryResourceForGuest(Long domainId, String type);
}
