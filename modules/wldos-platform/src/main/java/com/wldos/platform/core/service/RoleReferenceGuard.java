/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import com.wldos.platform.core.dao.RoleBindingDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.entity.WoRole;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.platform.support.rbac.RoleShareScope;

/**
 * 角色删除前引用校验。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class RoleReferenceGuard {

	private final RoleDao roleDao;
	private final RoleBindingDao roleBindingDao;
	private final JdbcOperations jdbcOperations;

	public RoleReferenceGuard(RoleDao roleDao, RoleBindingDao roleBindingDao, JdbcOperations jdbcOperations) {
		this.roleDao = roleDao;
		this.roleBindingDao = roleBindingDao;
		this.jdbcOperations = jdbcOperations;
	}

	public void assertDeletable(WoRole role) {
		if (role == null || role.getId() == null) {
			return;
		}
		long orgRefs = this.roleDao.countRoleOrgByRoleId(role.getId());
		if (orgRefs > 0) {
			throw new BaseException("角色仍被组织挂接，无法删除", ResultCode.FORBIDDEN.getCode());
		}
		long childRoles = countChildRoles(role.getId());
		if (childRoles > 0) {
			throw new BaseException("角色仍有子角色，无法删除", ResultCode.FORBIDDEN.getCode());
		}
		if (this.roleDao.countAuthRoleByRoleId(role.getId()) > 0) {
			throw new BaseException("角色仍被用户或主体授权引用，无法删除", ResultCode.FORBIDDEN.getCode());
		}
		if (this.roleBindingDao.countValidByRoleId(role.getId()) > 0) {
			throw new BaseException("角色仍有多态绑定引用，无法删除", ResultCode.FORBIDDEN.getCode());
		}
		if (RoleShareScope.isShared(role.getShareScope())) {
			long jurisdictionScopes = countJurisdictionOverlayScopes(role.getId());
			long jurisdictionAuth = countJurisdictionOverlayAuth(role.getId());
			long jurisdictionPolicy = countJurisdictionOverlayPolicy(role.getId());
			if (jurisdictionScopes > 0 || jurisdictionAuth > 0 || jurisdictionPolicy > 0) {
				throw new BaseException("SHARED 角色仍被其它站点定制引用，无法删除", ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	private long countJurisdictionOverlayScopes(Long roleId) {
		Long count = this.jdbcOperations.queryForObject(
				"select count(1) from wo_role_scope where role_id=? and delete_flag='normal' "
						+ "and (com_id is not null or domain_id is not null)",
				Long.class,
				roleId);
		return count == null ? 0L : count;
	}

	private long countJurisdictionOverlayAuth(Long roleId) {
		Long count = this.jdbcOperations.queryForObject(
				"select count(1) from wo_auth_role where role_id=? and delete_flag='normal' "
						+ "and (com_id is not null or domain_id is not null)",
				Long.class,
				roleId);
		return count == null ? 0L : count;
	}

	private long countJurisdictionOverlayPolicy(Long roleId) {
		Long count = this.jdbcOperations.queryForObject(
				"select count(1) from wo_role_data_policy_overlay where role_id=? and delete_flag='normal' "
						+ "and (com_id is not null or domain_id is not null)",
				Long.class,
				roleId);
		return count == null ? 0L : count;
	}

	private long countChildRoles(Long roleId) {
		Long count = this.jdbcOperations.queryForObject(
				"select count(1) from wo_role where parent_id=? and delete_flag='normal'",
				Long.class,
				roleId);
		return count == null ? 0L : count;
	}
}
