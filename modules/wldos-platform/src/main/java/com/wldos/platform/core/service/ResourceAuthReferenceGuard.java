/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wldos.platform.core.dao.AuthRoleDao;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

/**
 * 资源删除前角色授权引用校验。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class ResourceAuthReferenceGuard {

	private final AuthRoleDao authRoleDao;

	public ResourceAuthReferenceGuard(AuthRoleDao authRoleDao) {
		this.authRoleDao = authRoleDao;
	}

	public void assertDeletable(Long resourceId, Long appId, String resourceLabel) {
		if (resourceId == null) {
			return;
		}
		long count = this.authRoleDao.countActiveByResourceIdAndAppId(resourceId, appId);
		if (count <= 0) {
			return;
		}
		List<String> roleNames = this.authRoleDao.listActiveRoleNamesByResourceIdAndAppId(resourceId, appId);
		StringBuilder message = new StringBuilder("该资源已授权给角色，请先在角色管理中取消授权后再删除");
		if (roleNames != null && !roleNames.isEmpty()) {
			int limit = Math.min(3, roleNames.size());
			message.append("（");
			for (int i = 0; i < limit; i++) {
				if (i > 0) {
					message.append("、");
				}
				message.append(roleNames.get(i));
			}
			message.append("）");
		}
		throw new BaseException(message.toString(), ResultCode.VALIDATION_ERROR.getCode());
	}
}
