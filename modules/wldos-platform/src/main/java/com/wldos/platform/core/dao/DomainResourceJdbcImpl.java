/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.Collections;
import java.util.List;

import io.github.wldos.platform.support.domain.DomainResourceOpener;
import io.github.wldos.platform.support.domain.vo.DomainResource;

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

	@Autowired(required = false)
	@Lazy
	@Qualifier("domainResourceOpener")
	private DomainResourceOpener domainResourceOpener;

	@Override
	public List<DomainResource> queryDomainDynamicRoutes(Long domainId) {
		if (this.domainResourceOpener != null) {
			return this.domainResourceOpener.queryDomainDynamicRoutes(domainId);
		}
		// 开源版本：使用接口默认实现（返回空集合）
		return Collections.emptyList();
	}

	@Override
	public List<DomainResource> queryDomainResources(Long domainId) {
		if (this.domainResourceOpener != null) {
			return this.domainResourceOpener.queryDomainResources(domainId);
		}
		// 开源版本：使用接口默认实现（返回空集合）
		return Collections.emptyList();
	}
}