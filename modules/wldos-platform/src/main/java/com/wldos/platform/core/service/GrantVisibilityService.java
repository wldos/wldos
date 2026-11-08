/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.ComUserDao;
import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.entity.WoComUser;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.vo.Org;
import com.wldos.platform.core.vo.Role;

import java.util.List;
import io.github.wldos.common.enums.BoolEnum;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;
import io.github.wldos.platform.support.rbac.BoundDomainResolver;
import io.github.wldos.platform.support.rbac.GrantAction;
import io.github.wldos.platform.support.rbac.GrantClass;
import io.github.wldos.platform.support.rbac.GrantVisibilityContext;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 赋权可见性门面：从当前操作者构建 {@link GrantVisibilityContext} 并断言 LIST/ASSIGN/MANAGE/MEMBER_OPS。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class GrantVisibilityService extends NonEntityService {

	@Autowired
	@Lazy
	private UserDao userDao;

	@Autowired
	@Lazy
	private ComUserDao comUserDao;

	public GrantVisibilityContext contextFor(Long userId) {
		if (userId == null) {
			return new GrantVisibilityContext(AdminScopeLevel.MEMBER, null, null, false);
		}
		boolean platformAdmin = this.commonOperate.isAdmin(userId);
		AdminScopeLevel level = resolveLevel(userId);
		Long comId = resolveGrantorComId(userId);
		java.util.List<Long> bound = this.commonOperate.listBoundDomainIds(userId);
		if (level == AdminScopeLevel.DOMAIN) {
			bound = BoundDomainResolver.resolve(bound, this.getDomainId());
		}
		return new GrantVisibilityContext(
				level,
				comId,
				bound,
				platformAdmin);
	}

	public void assertOrgAllowed(Long userId, WoOrg org, GrantAction action) {
		if (org == null || userId == null) {
			return;
		}
		GrantVisibilityContext ctx = contextFor(userId);
		if (ctx.isPlatformSuperAdmin()) {
			return;
		}
		GrantClass cls = GrantVisibilityPolicy.classifyOrg(
				org.getOrgCode(), org.getOrgType(), org.getComId(), org.getDomainId(), org.isSystemReserved());
		if (!GrantVisibilityPolicy.allowed(ctx, cls, action, org.getComId(), org.getDomainId())) {
			throw new BaseException("无权操作该组织", ResultCode.FORBIDDEN.getCode());
		}
	}

	public void assertRoleAllowed(Long userId, WoRole role, GrantAction action) {
		if (role == null || userId == null) {
			return;
		}
		GrantVisibilityContext ctx = contextFor(userId);
		if (ctx.isPlatformSuperAdmin()) {
			return;
		}
		GrantClass cls = GrantVisibilityPolicy.classifyRole(
				role.getRoleCode(), role.getRoleType(), role.getComId(), role.getDomainId(), role.isSystemReserved());
		if (!GrantVisibilityPolicy.allowed(ctx, cls, action, role.getComId(), role.getDomainId())) {
			throw new BaseException("无权操作该角色", ResultCode.FORBIDDEN.getCode());
		}
	}

	public void assertUsersInGrantorScope(Long grantorId, Iterable<Long> userIds) {
		if (grantorId == null || userIds == null || this.commonOperate.isAdmin(grantorId)) {
			return;
		}
		GrantVisibilityContext ctx = contextFor(grantorId);
		if (ctx.getGrantorLevel().getRank() >= AdminScopeLevel.TENANT.getRank()) {
			return;
		}
		if (ctx.getGrantorLevel() != AdminScopeLevel.DOMAIN) {
			throw new BaseException("无权操作用户", ResultCode.FORBIDDEN.getCode());
		}
		for (Long userId : userIds) {
			if (userId == null) {
				continue;
			}
			WoUser user = this.userDao.findById(userId).orElse(null);
			if (user == null) {
				continue;
			}
			Long userDomainId = user.getDomainId();
			if (userDomainId == null || !ctx.getBoundDomainIds().contains(userDomainId)) {
				throw new BaseException("用户不在本站管理范围内", ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	private Long resolveGrantorComId(Long userId) {
		java.util.List<WoComUser> rows = this.comUserDao.queryAllByUserIds(
				java.util.Collections.singletonList(userId),
				ValidStatusEnum.VALID.toString(),
				DeleteFlagEnum.NORMAL.toString());
		if (rows != null) {
			for (WoComUser row : rows) {
				if (BoolEnum.YES.toString().equals(row.getIsMain()) && row.getComId() != null) {
					return row.getComId();
				}
			}
			if (!rows.isEmpty() && rows.get(0).getComId() != null) {
				return rows.get(0).getComId();
			}
		}
		return this.getTenantId();
	}

	private AdminScopeLevel resolveLevel(Long userId) {
		if (this.commonOperate.isAdmin(userId)) {
			return AdminScopeLevel.PLATFORM;
		}
		if (this.commonOperate.isTenantAdmin(userId)) {
			return AdminScopeLevel.TENANT;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			return AdminScopeLevel.DOMAIN;
		}
		if (this.commonOperate.getPrimaryOrgId(userId) != null) {
			return AdminScopeLevel.ORG;
		}
		return AdminScopeLevel.MEMBER;
	}

	public boolean isOrgVisible(Long userId, WoOrg org) {
		if (org == null) {
			return false;
		}
		GrantVisibilityContext ctx = contextFor(userId);
		return GrantVisibilityPolicy.visibleOrg(
				ctx, org.getOrgCode(), org.getOrgType(), org.getComId(), org.getDomainId(), org.isSystemReserved());
	}

	public boolean isRoleVisible(Long userId, WoRole role) {
		if (role == null) {
			return false;
		}
		GrantVisibilityContext ctx = contextFor(userId);
		return GrantVisibilityPolicy.visibleRole(
				ctx, role.getRoleCode(), role.getRoleType(), role.getComId(), role.getDomainId(), role.isSystemReserved());
	}

	public void filterWoRoles(Long userId, List<WoRole> roles) {
		if (roles == null || userId == null || this.commonOperate.isAdmin(userId)) {
			return;
		}
		roles.removeIf(r -> !isRoleVisible(userId, r));
	}

	public void filterWoOrgs(Long userId, List<WoOrg> orgs) {
		if (orgs == null || userId == null || this.commonOperate.isAdmin(userId)) {
			return;
		}
		orgs.removeIf(o -> !isOrgVisible(userId, o));
	}

	public void filterRoleTree(Long userId, List<Role> roles) {
		if (roles == null || userId == null || this.commonOperate.isAdmin(userId)) {
			return;
		}
		roles.removeIf(r -> !isRoleVisible(userId, toWoRole(r)));
		for (Role role : roles) {
			if (role.getChildren() != null && !role.getChildren().isEmpty()) {
				filterRoleTree(userId, role.getChildren());
			}
		}
	}

	public void filterOrgTree(Long userId, List<Org> orgs) {
		if (orgs == null || userId == null || this.commonOperate.isAdmin(userId)) {
			return;
		}
		orgs.removeIf(o -> !isOrgVisible(userId, toWoOrg(o)));
		for (Org org : orgs) {
			if (org.getChildren() != null && !org.getChildren().isEmpty()) {
				filterOrgTree(userId, org.getChildren());
			}
		}
	}

	/**
	 * 超管指定目标租户时，从角色树剔除平台内置身份角色。
	 */
	public void stripPlatformIdentityRoleTree(List<Role> roles, Long targetComId) {
		if (roles == null || targetComId == null || targetComId <= 0) {
			return;
		}
		stripIdentityRolesRecursive(roles);
	}

	/**
	 * 超管指定目标租户时，从组织树剔除平台内置身份组织。
	 */
	public void stripPlatformIdentityOrgTree(List<Org> orgs, Long targetComId) {
		if (orgs == null || targetComId == null || targetComId <= 0) {
			return;
		}
		stripIdentityOrgsRecursive(orgs);
	}

	public void stripPlatformIdentityWoRoles(java.util.List<WoRole> roles, Long targetComId) {
		if (roles == null || targetComId == null || targetComId <= 0) {
			return;
		}
		roles.removeIf(r -> r != null && GrantVisibilityPolicy.isIdentityRoleCode(r.getRoleCode()));
	}

	public void stripPlatformIdentityWoOrgs(java.util.List<WoOrg> orgs, Long targetComId) {
		if (orgs == null || targetComId == null || targetComId <= 0) {
			return;
		}
		orgs.removeIf(o -> o != null && GrantVisibilityPolicy.isIdentityOrgCode(o.getOrgCode()));
	}

	public Long parseScopedTargetComId(java.util.Map<String, Object> params) {
		if (params == null || !params.containsKey("comId")) {
			return null;
		}
		Object raw = params.get("comId");
		if (raw == null || ObjectUtils.isBlank(raw.toString())) {
			return null;
		}
		try {
			long comId = Long.parseLong(raw.toString().trim());
			return comId >= 0 ? comId : null;
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 超管列表：comId=0 只看平台行；comId>0 只看该租户行（仍含 comId=0 的共享模板行）。
	 */
	public void filterOrgTreeByScopedComId(List<Org> orgs, Long scopedComId) {
		if (orgs == null || scopedComId == null) {
			return;
		}
		java.util.Iterator<Org> iterator = orgs.iterator();
		while (iterator.hasNext()) {
			Org org = iterator.next();
			if (org == null) {
				iterator.remove();
				continue;
			}
			if (org.getChildren() != null && !org.getChildren().isEmpty()) {
				this.filterOrgTreeByScopedComId(org.getChildren(), scopedComId);
			}
			if (!this.matchesScopedComId(org.getComId(), scopedComId)) {
				iterator.remove();
			}
		}
	}

	public void filterRoleTreeByScopedComId(List<Role> roles, Long scopedComId) {
		if (roles == null || scopedComId == null) {
			return;
		}
		java.util.Iterator<Role> iterator = roles.iterator();
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role == null) {
				iterator.remove();
				continue;
			}
			if (role.getChildren() != null && !role.getChildren().isEmpty()) {
				this.filterRoleTreeByScopedComId(role.getChildren(), scopedComId);
			}
			if (!this.matchesScopedComId(role.getComId(), scopedComId)) {
				iterator.remove();
			}
		}
	}

	private boolean matchesScopedComId(Long rowComId, long scopedComId) {
		if (scopedComId == 0) {
			return rowComId == null || rowComId <= 0;
		}
		if (rowComId == null || rowComId <= 0) {
			return true;
		}
		return rowComId == scopedComId;
	}

	/** 从当前 HTTP 请求读取可选 {@code comId}（供 {@code GET .../all} 覆写端点使用）。 */
	public Long parseScopedTargetComIdFromRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			return null;
		}
		String raw = attrs.getRequest().getParameter("comId");
		if (ObjectUtils.isBlank(raw)) {
			return null;
		}
		java.util.Map<String, Object> params = new java.util.HashMap<>(1);
		params.put("comId", raw);
		return parseScopedTargetComId(params);
	}

	private void stripIdentityRolesRecursive(List<Role> roles) {
		java.util.Iterator<Role> iterator = roles.iterator();
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role == null) {
				iterator.remove();
				continue;
			}
			if (role.getChildren() != null && !role.getChildren().isEmpty()) {
				stripIdentityRolesRecursive(role.getChildren());
			}
			if (GrantVisibilityPolicy.isIdentityRoleCode(role.getRoleCode())) {
				iterator.remove();
			}
		}
	}

	private void stripIdentityOrgsRecursive(List<Org> orgs) {
		java.util.Iterator<Org> iterator = orgs.iterator();
		while (iterator.hasNext()) {
			Org org = iterator.next();
			if (org == null) {
				iterator.remove();
				continue;
			}
			if (org.getChildren() != null && !org.getChildren().isEmpty()) {
				stripIdentityOrgsRecursive(org.getChildren());
			}
			if (GrantVisibilityPolicy.isIdentityOrgCode(org.getOrgCode())) {
				iterator.remove();
			}
		}
	}

	private WoRole toWoRole(Role role) {
		if (role == null) {
			return null;
		}
		WoRole wo = new WoRole();
		wo.setId(role.getId());
		wo.setRoleCode(role.getRoleCode());
		wo.setRoleType(role.getRoleType());
		wo.setComId(role.getComId());
		wo.setDomainId(role.getDomainId());
		return wo;
	}

	private WoOrg toWoOrg(Org org) {
		if (org == null) {
			return null;
		}
		WoOrg wo = new WoOrg();
		wo.setId(org.getId());
		wo.setOrgCode(org.getOrgCode());
		wo.setOrgType(org.getOrgType());
		wo.setComId(org.getComId());
		wo.setDomainId(org.getDomainId());
		return wo;
	}
}
