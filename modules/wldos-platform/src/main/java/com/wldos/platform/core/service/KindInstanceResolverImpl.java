/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoRole;

import io.github.wldos.platform.support.rbac.kind.KindInstanceResolver;
import io.github.wldos.platform.support.rbac.kind.SystemOrgKind;
import io.github.wldos.platform.support.rbac.kind.SystemRoleKind;

import org.springframework.stereotype.Service;

/**
 * 按租户、站点解析内置会员角色/分群主键。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class KindInstanceResolverImpl implements KindInstanceResolver {

	private final RoleDao roleDao;

	private final OrgDao orgDao;

	public KindInstanceResolverImpl(RoleDao roleDao, OrgDao orgDao) {
		this.roleDao = roleDao;
		this.orgDao = orgDao;
	}

	@Override
	public Long resolveRoleKind(Long comId, Long domainId, SystemRoleKind kind) {
		if (comId == null || domainId == null || domainId <= 0 || kind == null) {
			return null;
		}
		WoRole role = this.roleDao.findByComIdAndDomainIdAndRoleCode(comId, domainId, kind.getRoleCode());
		return role != null ? role.getId() : null;
	}

	@Override
	public Long resolveOrgKind(Long comId, Long domainId, SystemOrgKind kind) {
		if (comId == null || domainId == null || domainId <= 0 || kind == null) {
			return null;
		}
		WoOrg org = this.orgDao.findByComIdAndDomainIdAndOrgCode(comId, domainId, kind.getOrgCode());
		return org != null ? org.getId() : null;
	}

	@Override
	public boolean hasSiteKindInstances(Long comId, Long domainId) {
		if (comId == null || domainId == null || domainId <= 0) {
			return false;
		}
		List<String> codes = Arrays.stream(SystemRoleKind.siteBootstrapKinds())
				.map(SystemRoleKind::getRoleCode)
				.collect(Collectors.toList());
		return this.roleDao.countOperationalByComIdAndDomainId(comId, domainId, codes) >= codes.size();
	}
}
