/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import org.springframework.stereotype.Service;

import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.dao.RoleOrgDao;
import com.wldos.platform.core.entity.WoOrg;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

/**
 * 组织（含会员分群）删除前引用校验。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class OrgReferenceGuard {

	private final OrgDao orgDao;
	private final OrgUserDao orgUserDao;
	private final RoleOrgDao roleOrgDao;

	public OrgReferenceGuard(OrgDao orgDao, OrgUserDao orgUserDao, RoleOrgDao roleOrgDao) {
		this.orgDao = orgDao;
		this.orgUserDao = orgUserDao;
		this.roleOrgDao = roleOrgDao;
	}

	public void assertDeletable(WoOrg org) {
		if (org == null || org.getId() == null) {
			return;
		}
		Long orgId = org.getId();
		if (this.orgDao.countNormalChildrenByParentId(orgId) > 0) {
			throw new BaseException("存在下级组织，请先处理子组织后再删除", ResultCode.FORBIDDEN.getCode());
		}
		if (this.orgUserDao.countNormalMembersByOrgId(orgId) > 0) {
			throw new BaseException("组织仍有成员，请先移除成员后再删除", ResultCode.FORBIDDEN.getCode());
		}
		if (this.roleOrgDao.countValidByOrgId(orgId) > 0) {
			throw new BaseException("组织仍被角色挂接，请先解除角色关联后再删除", ResultCode.FORBIDDEN.getCode());
		}
	}
}
