/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.auth.service;

import java.util.Objects;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.service.DomainService;
import com.wldos.platform.core.service.PlatformOperatorSettingsService;
import com.wldos.platform.core.service.UserService;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

import org.springframework.stereotype.Service;

/**
 * 默认登录 Host 规则：平台超管组不限；平台层账号仅主站；租户账号仅本租户站点域。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class DefaultLoginHostAdmissionPolicy extends NonEntityService implements LoginHostAdmissionPolicy {

	private final UserService userService;

	private final DomainService domainService;

	private final PlatformOperatorSettingsService operatorSettings;

	public DefaultLoginHostAdmissionPolicy(
			UserService userService,
			DomainService domainService,
			PlatformOperatorSettingsService operatorSettings) {
		this.userService = userService;
		this.domainService = domainService;
		this.operatorSettings = operatorSettings;
	}

	@Override
	public void assertPasswordLoginAllowed(Long userId, Long requestDomainId, Long contextComId) {
		if (userId == null || userId <= 0) {
			return;
		}
		if (requestDomainId == null || requestDomainId <= 0) {
			throw new BaseException("无法识别当前站点，拒绝登录", ResultCode.FORBIDDEN.getCode());
		}
		if (this.isAdmin(userId)) {
			return;
		}
		WoUser user = this.userService.findById(userId);
		if (user == null) {
			throw new BaseException("用户不存在", ResultCode.UNAUTHORIZED.getCode());
		}
		Long mainComId = user.getMainComId();
		if (mainComId == null || mainComId <= 0 || Objects.equals(mainComId, Constants.TOP_COM_ID)) {
			assertPlatformLayerLogin(requestDomainId);
			return;
		}
		if (this.operatorSettings.isPlatformOperatorCom(mainComId)) {
			assertDomainOwnedByCom(requestDomainId, mainComId);
			return;
		}
		assertDomainOwnedByCom(requestDomainId, mainComId);
	}

	private void assertPlatformLayerLogin(Long requestDomainId) {
		Long primaryDomainId = this.operatorSettings.findPrimaryDomainId().orElse(null);
		if (primaryDomainId == null || primaryDomainId <= 0) {
			throw new BaseException("平台主站未配置，无法登录", ResultCode.OPERATION_FAILED.getCode());
		}
		if (!primaryDomainId.equals(requestDomainId)) {
			throw new BaseException("平台账号仅可在平台主站登录", ResultCode.FORBIDDEN.getCode());
		}
	}

	private void assertDomainOwnedByCom(Long requestDomainId, Long expectedComId) {
		WoDomain domain = this.domainService.findById(requestDomainId);
		if (domain == null || domain.getComId() == null) {
			throw new BaseException("当前站点无效，拒绝登录", ResultCode.FORBIDDEN.getCode());
		}
		if (!expectedComId.equals(domain.getComId())) {
			throw new BaseException("请在本租户站点登录，勿使用其它域名", ResultCode.FORBIDDEN.getCode());
		}
	}
}
