/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.List;
import java.util.Set;

import com.wldos.framework.mvc.service.NonEntityService;

import io.github.wldos.common.Constants;
import io.github.wldos.platform.support.rbac.UserRoleMembershipQueryHelper;
import io.github.wldos.platform.support.rbac.slot.BusinessSlot;
import io.github.wldos.platform.support.rbac.slot.SlotResolver;
import io.github.wldos.platform.support.rbac.slot.SlotTargetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

/**
 * 当前用户是否属于插槽解析出的目标角色/角色集（含角色链祖先匹配）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class SlotUserMembershipService extends NonEntityService {

	@Autowired
	@Lazy
	private SlotResolver slotResolver;

	@Autowired
	@Lazy
	private UserService userService;

	@Autowired
	private JdbcOperations jdbcOperations;

	public boolean userMatchesSlot(Long userId, String slotCode, Long comId, Long domainId) {
		if (userId == null) {
			return false;
		}
		if (this.userService.isAdmin(userId)) {
			return true;
		}
		BusinessSlot slot = BusinessSlot.ofSlotCode(slotCode);
		if (slot == null) {
			return false;
		}
		Long tenantId = comId != null ? comId : Constants.TOP_COM_ID;
		List<Long> targets = this.slotResolver.resolveTargets(slotCode, tenantId, domainId);
		if (targets.isEmpty()) {
			return false;
		}
		if (slot.getTargetType() == SlotTargetType.ORG) {
			return false;
		}
		Set<Long> userRoles = UserRoleMembershipQueryHelper.loadEffectiveRoleIds(
				this.jdbcOperations, userId, this.bindingChannelEnabled, this.legacyChannelsEnabled);
		return UserRoleMembershipQueryHelper.userHasAnyRoleId(userRoles, targets);
	}

	public boolean userHasCmsCreatorCapabilities(Long userId, Long comId, Long domainId) {
		return userMatchesSlot(userId, BusinessSlot.CMS_CREATOR_CAPABILITIES.getSlotCode(), comId, domainId);
	}
}
