/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.gateway;

import javax.servlet.http.HttpServletRequest;

import io.github.wldos.common.utils.http.IpUtils;

/**
 * 网关模块对 {@link IpUtils#getClientIp(HttpServletRequest)} 的别名入口，便于 edge/审计代码按网关语义引用。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class GatewayClientIpResolver {

	private GatewayClientIpResolver() {
	}

	public static String resolve(HttpServletRequest request) {
		return IpUtils.getClientIp(request);
	}
}
