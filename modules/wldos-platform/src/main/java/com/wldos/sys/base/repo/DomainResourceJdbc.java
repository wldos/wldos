/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.sys.base.repo;

import java.util.List;

import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.support.domain.DomainResourceOpener;
import com.wldos.support.domain.vo.DomainResource;

/**
 * 域资源jdbc扩展。
 *
 * @author 元悉宇宙
 * @date 2021/12/23
 * @version 1.0
 */
public interface DomainResourceJdbc extends DomainResourceOpener {
	/**
	 * 查询某域的动态路由资源，获取指定域的动态路由资源，路由共享，组件专有，业务分类专有。
	 * 不共享组件是为了多域应用隔离，应用包括视图、API两个层面，1.0仅考虑视图层面。
	 *
	 * @return 域的动态路由资源
	 */
	List<DomainResource> queryDomainDynamicRoutes(Long domainId);

	/**
	 * 查询域关联的所有资源
	 *
	 * @param domainId 域id
	 * @return 域资源列表
	 */
	List<DomainResource> queryDomainResources(Long domainId);

	default String queryDomainDynamicRoutesCacheKey(Long domainId) {
		return RedisKeyEnum.WLDOS_DOMAIN_RES.toString() + domainId + "dynamic";
	}
}
