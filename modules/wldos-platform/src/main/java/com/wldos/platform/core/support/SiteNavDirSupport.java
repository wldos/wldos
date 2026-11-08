/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.support;

import io.github.wldos.platform.support.resource.entity.WoResource;
import io.github.wldos.platform.support.resource.enums.MenuNodeKindEnum;
import io.github.wldos.platform.support.rbac.ResourceScopeLevel;

/**
 * 站点自建 nav_dir（scope_level=domain）判定。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class SiteNavDirSupport {

	private SiteNavDirSupport() {
	}

	public static boolean isSiteOwnedNavDir(WoResource resource) {
		if (resource == null) {
			return false;
		}
		return MenuNodeKindEnum.isNavDir(resource.getMenuNodeKind())
				&& ResourceScopeLevel.DOMAIN.getCode().equals(resource.getScopeLevel());
	}
}
