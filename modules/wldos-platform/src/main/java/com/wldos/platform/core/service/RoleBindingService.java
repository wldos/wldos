/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;

import com.wldos.platform.core.dao.RoleBindingDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoRoleBinding;
import com.wldos.platform.core.entity.WoUser;

import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.framework.common.IDGen;
import com.wldos.framework.mvc.service.NonEntityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 人直挂角色（wo_role_binding target_type=user）。
 * 校验：目标用户身份层级低于操作人（D9）+ 角色在操作者可授范围；绑定后刷新授权缓存。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class RoleBindingService extends NonEntityService {

	@Autowired
	private RoleBindingDao roleBindingDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AdminScopeService adminScopeService;

	/**
	 * 人直挂角色。重复绑定幂等跳过。
	 *
	 * @param userId 目标用户 id
	 * @param roleId 角色 id
	 */
	@Transactional(rollbackFor = Exception.class)
	public void bindUserRole(Long userId, Long roleId) {
		Long operatorId = this.commonOperate.getUserId();
		WoUser target = this.userDao.findById(userId).orElse(null);
		if (target == null) {
			throw new BaseException("目标用户不存在", ResultCode.DATA_NOT_FOUND.getCode());
		}
		this.adminScopeService.assertTargetIdentityLowerThanOperator(operatorId, userId);
		WoRole role = this.roleDao.findById(roleId).orElse(null);
		if (role == null || !ValidStatusEnum.VALID.toString().equals(role.getIsValid())
				|| !DeleteFlagEnum.NORMAL.toString().equals(role.getDeleteFlag())) {
			throw new BaseException("角色不存在或已失效", ResultCode.DATA_NOT_FOUND.getCode());
		}
		this.adminScopeService.assertCanAssignRole(operatorId, role);
		if (this.roleBindingDao.findValidOne(roleId, WoRoleBinding.TARGET_USER, userId) != null) {
			return;
		}
		WoRoleBinding binding = new WoRoleBinding();
		binding.setId(IDGen.nextId());
		binding.setRoleId(roleId);
		binding.setTargetType(WoRoleBinding.TARGET_USER);
		binding.setTargetId(userId);
		binding.setComId(this.getTenantId());
		binding.setArchId(0L);
		binding.setIsValid(ValidStatusEnum.VALID.toString());
		binding.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		this.roleBindingDao.save(binding);
		this.refreshAuth();
	}

	/**
	 * 解绑直挂角色（软删绑定行）。
	 *
	 * @param userId 目标用户 id
	 * @param roleId 角色 id
	 */
	@Transactional(rollbackFor = Exception.class)
	public void unbindUserRole(Long userId, Long roleId) {
		Long operatorId = this.commonOperate.getUserId();
		this.adminScopeService.assertTargetIdentityLowerThanOperator(operatorId, userId);
		WoRoleBinding binding = this.roleBindingDao.findValidOne(roleId, WoRoleBinding.TARGET_USER, userId);
		if (binding == null) {
			return;
		}
		binding.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		this.roleBindingDao.save(binding);
		this.refreshAuth();
	}

	/** 用户直挂角色绑定清单（含已失效角色行，供管理端展示后清理） */
	public List<WoRoleBinding> listUserDirectBindings(Long userId) {
		return this.roleBindingDao.findValidByTarget(WoRoleBinding.TARGET_USER, userId);
	}
}
