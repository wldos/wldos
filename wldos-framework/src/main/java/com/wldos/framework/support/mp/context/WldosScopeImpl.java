/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.mp.context;

import io.github.wldos.common.res.PageQuery;
import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.platform.support.mp.context.WldosScope;
import io.github.wldos.platform.support.rbac.DataScopeProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * {@link WldosScope} 默认实现。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class WldosScopeImpl implements WldosScope {

	@Autowired
	@Lazy
	private CommonOperation commonOperation;

	@Override
	public void applyTenantFilter(PageQuery pageQuery) {
		commonOperation.applyTenantFilter(pageQuery);
	}

	@Override
	public void applyDomainFilter(PageQuery pageQuery) {
		commonOperation.applyDomainFilter(pageQuery);
	}

	@Override
	public void applyPortalDomainScope(PageQuery pageQuery) {
		commonOperation.applyPortalDomainScope(pageQuery);
	}

	@Override
	public void applyScope(PageQuery pageQuery, DataScopeProfile profile, Class<?> entityClass) {
		commonOperation.applyScope(pageQuery, profile, entityClass);
	}
}
