/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.platform.core.vo.Org;
import com.wldos.platform.core.vo.Role;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;

/**
 * 同站同编码的内置分群/角色去重，优先保留已绑定站点的实例。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class OperationalTreeDeduper {

	private OperationalTreeDeduper() {
	}

	public static void dedupeOrgTree(List<Org> orgs, Long targetDomainId) {
		if (orgs == null || targetDomainId == null) {
			return;
		}
		for (Org org : orgs) {
			if (org.getChildren() != null && !org.getChildren().isEmpty()) {
				dedupeOrgTree(org.getChildren(), targetDomainId);
			}
		}
		Map<String, Org> bestByCode = new HashMap<>();
		List<Org> removals = new ArrayList<>();
		for (Org org : orgs) {
			if (org == null || org.getOrgCode() == null || !GrantVisibilityPolicy.isOperationalOrgCode(org.getOrgCode())) {
				continue;
			}
			Org existing = bestByCode.get(org.getOrgCode());
			if (existing == null) {
				bestByCode.put(org.getOrgCode(), org);
				continue;
			}
			Org winner = preferOrg(existing, org, targetDomainId);
			removals.add(winner == existing ? org : existing);
			bestByCode.put(org.getOrgCode(), winner);
		}
		orgs.removeAll(removals);
	}

	public static void dedupeRoleTree(List<Role> roles, Long targetDomainId) {
		if (roles == null || targetDomainId == null) {
			return;
		}
		for (Role role : roles) {
			if (role.getChildren() != null && !role.getChildren().isEmpty()) {
				dedupeRoleTree(role.getChildren(), targetDomainId);
			}
		}
		Map<String, Role> bestByCode = new HashMap<>();
		List<Role> removals = new ArrayList<>();
		for (Role role : roles) {
			if (role == null || role.getRoleCode() == null || !GrantVisibilityPolicy.isOperationalRoleCode(role.getRoleCode())) {
				continue;
			}
			Role existing = bestByCode.get(role.getRoleCode());
			if (existing == null) {
				bestByCode.put(role.getRoleCode(), role);
				continue;
			}
			Role winner = preferRole(existing, role, targetDomainId);
			removals.add(winner == existing ? role : existing);
			bestByCode.put(role.getRoleCode(), winner);
		}
		roles.removeAll(removals);
	}

	private static Org preferOrg(Org a, Org b, Long targetDomainId) {
		boolean aBound = isTargetInstance(a.getDomainId(), targetDomainId);
		boolean bBound = isTargetInstance(b.getDomainId(), targetDomainId);
		if (aBound && !bBound) {
			return a;
		}
		if (bBound && !aBound) {
			return b;
		}
		return preferRicherLabel(a.getOrgName(), b.getOrgName()) ? a : b;
	}

	private static Role preferRole(Role a, Role b, Long targetDomainId) {
		boolean aBound = isTargetInstance(a.getDomainId(), targetDomainId);
		boolean bBound = isTargetInstance(b.getDomainId(), targetDomainId);
		if (aBound && !bBound) {
			return a;
		}
		if (bBound && !aBound) {
			return b;
		}
		return preferRicherLabel(a.getRoleDesc(), b.getRoleDesc()) ? a : b;
	}

	private static boolean preferRicherLabel(String a, String b) {
		boolean aRich = hasMeaningfulText(a);
		boolean bRich = hasMeaningfulText(b);
		if (aRich && !bRich) {
			return true;
		}
		if (bRich && !aRich) {
			return false;
		}
		return true;
	}

	private static boolean hasMeaningfulText(String text) {
		return text != null && !text.trim().isEmpty() && !"-".equals(text.trim());
	}

	private static boolean isTargetInstance(Long domainId, Long targetDomainId) {
		return domainId != null && domainId > 0 && targetDomainId.equals(domainId);
	}
}
