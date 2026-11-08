/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;
import java.util.Map;

import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoRole;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.kind.SystemRoleKind;

import org.springframework.stereotype.Service;

/**
 * 内置会员角色：列表 role_code 与站点实例 role_id 的解析。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class RoleOperationalKindBridge {

	private final KindInstanceResolverImpl kindInstanceResolver;

	private final RoleDao roleDao;

	private final DomainDao domainDao;

	public RoleOperationalKindBridge(
			KindInstanceResolverImpl kindInstanceResolver,
			RoleDao roleDao,
			DomainDao domainDao) {
		this.kindInstanceResolver = kindInstanceResolver;
		this.roleDao = roleDao;
		this.domainDao = domainDao;
	}

	public boolean isOperationalKind(WoRole role) {
		return role != null && role.getRoleCode() != null
				&& GrantVisibilityPolicy.isOperationalRoleCode(role.getRoleCode());
	}

	public Long parseTargetDomainId(Map<String, ?> params, Long contextDomainId, List<Long> boundDomainIds) {
		return parseTargetDomainId(params, contextDomainId, boundDomainIds, false);
	}

	/**
	 * @param sessionSiteOnly 站点管：忽略请求参数，固定当前访问站点
	 */
	public Long parseTargetDomainId(
			Map<String, ?> params,
			Long contextDomainId,
			List<Long> boundDomainIds,
			boolean sessionSiteOnly) {
		if (sessionSiteOnly && contextDomainId != null && contextDomainId > 0) {
			return contextDomainId;
		}
		Long fromParams = parseLongParam(params, "targetDomainId");
		if (fromParams == null) {
			fromParams = parseLongParam(params, "domainId");
		}
		if (fromParams != null && fromParams > 0) {
			return fromParams;
		}
		if (boundDomainIds != null && boundDomainIds.size() == 1) {
			return boundDomainIds.get(0);
		}
		if (contextDomainId != null && contextDomainId > 0) {
			return contextDomainId;
		}
		return null;
	}

	public Long resolveComIdForDomain(Long domainId, Long fallbackComId) {
		if (domainId == null || domainId <= 0) {
			return fallbackComId;
		}
		return this.domainDao.findById(domainId)
				.map(WoDomain::getComId)
				.orElse(fallbackComId);
	}

	/**
	 * 列表 reconcile：仅 siteBootstrapKinds；缺实例返回 null，不抛错。
	 */
	public WoRole tryResolveOperationalInstance(WoRole role, Long targetDomainId, Long comId) {
		if (role == null || targetDomainId == null || targetDomainId <= 0 || !isOperationalKind(role)) {
			return role;
		}
		SystemRoleKind kind = SystemRoleKind.ofRoleCode(role.getRoleCode());
		if (kind == null || !isSiteBootstrapKind(kind)) {
			return role;
		}
		return resolveOperationalInstanceInternal(role, targetDomainId, comId, kind);
	}

	/**
	 * 写操作：内置会员角色/分群；缺站点实例则报错。
	 */
	public WoRole resolveOperationalInstance(WoRole role, Long targetDomainId, Long comId) {
		if (role == null || targetDomainId == null || targetDomainId <= 0 || !isOperationalKind(role)) {
			return role;
		}
		SystemRoleKind kind = SystemRoleKind.ofRoleCode(role.getRoleCode());
		if (kind == null) {
			return role;
		}
		WoRole resolved = resolveOperationalInstanceInternal(role, targetDomainId, comId, kind);
		if (resolved == null) {
			throw new BaseException("目标站点尚未初始化运营角色实例，请先保存站点或重启自愈", ResultCode.VALIDATION_ERROR.getCode());
		}
		return resolved;
	}

	public static boolean isSiteBootstrapKind(SystemRoleKind kind) {
		if (kind == null) {
			return false;
		}
		for (SystemRoleKind bootstrap : SystemRoleKind.siteBootstrapKinds()) {
			if (bootstrap == kind) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSiteBootstrapRoleCode(String roleCode) {
		return isSiteBootstrapKind(SystemRoleKind.ofRoleCode(roleCode));
	}

	private WoRole resolveOperationalInstanceInternal(
			WoRole role,
			Long targetDomainId,
			Long comId,
			SystemRoleKind kind) {
		Long effectiveComId = resolveComIdForDomain(targetDomainId, comId);
		if (role.getDomainId() != null && role.getDomainId().equals(targetDomainId)
				&& role.getComId() != null && role.getComId().equals(effectiveComId)) {
			return role;
		}
		Long instanceId = this.kindInstanceResolver.resolveRoleKind(effectiveComId, targetDomainId, kind);
		if (instanceId == null) {
			return null;
		}
		return this.roleDao.findById(instanceId).orElse(role);
	}

	private static Long parseLongParam(Map<String, ?> params, String key) {
		if (params == null || !params.containsKey(key) || params.get(key) == null) {
			return null;
		}
		Object raw = params.get(key);
		if (raw instanceof Number) {
			return ((Number) raw).longValue();
		}
		String text = raw.toString().trim();
		if (text.isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(text);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}
}
