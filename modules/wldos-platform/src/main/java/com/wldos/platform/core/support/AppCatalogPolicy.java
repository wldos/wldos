/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.support;

import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.enums.AppTypeEnum;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

/**
 * 应用目录与站点挂载：平台内置 vs 租户可选。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class AppCatalogPolicy {

	/** 系统管理应用编码（app_code=admin，平台内置） */
	public static final String SITE_ESSENTIAL_PLATFORM_APP_CODE = "admin";

	private AppCatalogPolicy() {
	}

	public static boolean isSiteEssentialPlatformApp(WoApp app) {
		return app != null && SITE_ESSENTIAL_PLATFORM_APP_CODE.equals(app.getAppCode());
	}

	public static boolean isPlatformBuiltinApp(WoApp app) {
		return app != null && AppTypeEnum.PLATFORM.getValue().equals(app.getAppType());
	}

	public static boolean canViewPlatformAppTypeInCatalog(boolean superAdminViewer) {
		return superAdminViewer;
	}

	public static boolean canSelectAppForDomainMount(WoApp app, boolean superAdminViewer) {
		if (app == null) {
			return false;
		}
		if (superAdminViewer) {
			return true;
		}
		return !isPlatformBuiltinApp(app);
	}

	public static void assertDomainMountAllowed(WoApp app, boolean superAdminViewer) {
		if (app == null) {
			throw new BaseException("应用不存在", ResultCode.DATA_NOT_FOUND.getCode());
		}
		if (canSelectAppForDomainMount(app, superAdminViewer)) {
			return;
		}
		throw new BaseException("无权为该站点添加平台内置应用（系统管理仅超管可挂载）", ResultCode.FORBIDDEN.getCode());
	}
}
