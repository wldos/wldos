/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.mp.context;

import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.platform.support.mp.context.WldosContext;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * {@link WldosContext} 默认实现。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class WldosContextImpl implements WldosContext {

	@Autowired
	@Lazy
	private CommonOperation commonOperation;

	@Override
	public Long getUserId() {
		return commonOperation.getUserId();
	}

	@Override
	public String getUserIp() {
		return commonOperation.getUserIp();
	}

	@Override
	public String getToken() {
		return commonOperation.getToken();
	}

	@Override
	public Long getTenantId() {
		return commonOperation.getTenantId();
	}

	@Override
	public Long getDomainId() {
		return commonOperation.getDomainId();
	}

	@Override
	public String getDomain() {
		return commonOperation.getDomain();
	}

	@Override
	public boolean isMultiTenancy() {
		return commonOperation.isMultiTenancy();
	}

	@Override
	public boolean isMultiDomain() {
		return commonOperation.isMultiDomain();
	}

	@Override
	public Long getWorkComId() {
		return commonOperation.getWorkComId();
	}

	@Override
	public Long getOperatorComId(Long userId) {
		return commonOperation.getOperatorComId(userId);
	}

	@Override
	public boolean isAdmin(Long userId) {
		return commonOperation.isAdmin(userId);
	}

	@Override
	public boolean isTenantAdmin(Long userId) {
		return commonOperation.isTenantAdmin(userId);
	}

	@Override
	public boolean isDomainAdmin(Long userId) {
		return commonOperation.isDomainAdmin(userId);
	}

	@Override
	public boolean isCanTrust(Long userId) {
		return commonOperation.isCanTrust(userId);
	}

	@Override
	public AdminScopeLevel resolveOperatorLevel() {
		return commonOperation.resolveOperatorLevel();
	}
}
