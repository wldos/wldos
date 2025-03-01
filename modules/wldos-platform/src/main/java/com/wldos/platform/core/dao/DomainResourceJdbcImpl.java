/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.platform.support.domain.DomainResourceOpener;
import com.wldos.platform.support.domain.vo.DomainResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

/**
 * 域资源jdbc扩展实现类。
 *
 * @author 元悉宇宙
 * @date 2021/12/23
 * @version 1.0
 */
@SuppressWarnings("unused")
public class DomainResourceJdbcImpl implements DomainResourceJdbc {

	@Autowired
	@Lazy
	@Qualifier("domainResourceOpener")
	private DomainResourceOpener domainResourceOpener;

	@Override
	public List<DomainResource> queryDomainDynamicRoutes(Long domainId) {
		return this.domainResourceOpener.queryDomainDynamicRoutes(domainId);
	}

	@Override
	public List<DomainResource> queryDomainResources(Long domainId) {
		return this.domainResourceOpener.queryDomainResources(domainId);
	}
}