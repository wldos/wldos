/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoOrg;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.kind.SystemOrgKind;

import org.springframework.stereotype.Service;

/**
 * 内置会员分群：列表 org_code 与站点实例 org_id 的解析。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class OrgOperationalKindBridge {

	private final KindInstanceResolverImpl kindInstanceResolver;

	private final OrgDao orgDao;

	private final DomainDao domainDao;

	public OrgOperationalKindBridge(
			KindInstanceResolverImpl kindInstanceResolver,
			OrgDao orgDao,
			DomainDao domainDao) {
		this.kindInstanceResolver = kindInstanceResolver;
		this.orgDao = orgDao;
		this.domainDao = domainDao;
	}

	public boolean isOperationalKind(WoOrg org) {
		return org != null && org.getOrgCode() != null
				&& GrantVisibilityPolicy.isOperationalOrgCode(org.getOrgCode());
	}

	public Long resolveComIdForDomain(Long domainId, Long fallbackComId) {
		if (domainId == null || domainId <= 0) {
			return fallbackComId;
		}
		return this.domainDao.findById(domainId)
				.map(WoDomain::getComId)
				.orElse(fallbackComId);
	}

	public WoOrg tryResolveOperationalInstance(WoOrg org, Long targetDomainId, Long comId) {
		if (org == null || targetDomainId == null || targetDomainId <= 0 || !isOperationalKind(org)) {
			return org;
		}
		SystemOrgKind kind = SystemOrgKind.ofOrgCode(org.getOrgCode());
		if (kind == null || !isSiteBootstrapKind(kind)) {
			return org;
		}
		return resolveOperationalInstanceInternal(org, targetDomainId, comId, kind);
	}

	public WoOrg resolveOperationalInstance(WoOrg org, Long targetDomainId, Long comId) {
		if (org == null || targetDomainId == null || targetDomainId <= 0 || !isOperationalKind(org)) {
			return org;
		}
		SystemOrgKind kind = SystemOrgKind.ofOrgCode(org.getOrgCode());
		if (kind == null) {
			return org;
		}
		WoOrg resolved = resolveOperationalInstanceInternal(org, targetDomainId, comId, kind);
		if (resolved == null) {
			throw new BaseException("目标站点尚未初始化运营组织实例，请先保存站点或重启自愈", ResultCode.VALIDATION_ERROR.getCode());
		}
		return resolved;
	}

	/**
	 * 分群成员：模板行 + 目标站点 → 站点实例组织。
	 */
	public WoOrg resolveMemberOpsOrg(WoOrg org, Long domainId, Long requestComId, Long operatorTenantId) {
		if (org == null || !this.isOperationalKind(org)) {
			return org;
		}
		if (org.getDomainId() != null && org.getDomainId() > 0) {
			return org;
		}
		if (domainId == null || domainId <= 0) {
			return org;
		}
		Long scopeComId = requestComId;
		if (scopeComId == null || scopeComId <= 0) {
			scopeComId = this.resolveComIdForDomain(domainId, operatorTenantId);
		}
		WoOrg instance = this.tryResolveOperationalInstance(org, domainId, scopeComId);
		return instance != null ? instance : org;
	}

	public static boolean isSiteBootstrapKind(SystemOrgKind kind) {
		if (kind == null) {
			return false;
		}
		for (SystemOrgKind bootstrap : SystemOrgKind.siteBootstrapKinds()) {
			if (bootstrap == kind) {
				return true;
			}
		}
		return false;
	}

	private WoOrg resolveOperationalInstanceInternal(
			WoOrg org,
			Long targetDomainId,
			Long comId,
			SystemOrgKind kind) {
		Long effectiveComId = resolveComIdForDomain(targetDomainId, comId);
		if (org.getDomainId() != null && org.getDomainId().equals(targetDomainId)
				&& org.getComId() != null && org.getComId().equals(effectiveComId)) {
			return org;
		}
		Long instanceId = this.kindInstanceResolver.resolveOrgKind(effectiveComId, targetDomainId, kind);
		if (instanceId == null) {
			return null;
		}
		return this.orgDao.findById(instanceId).orElse(org);
	}
}
