/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

/**
 * 本机回环地址判定，供网关限流与 IP 策略共用。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class EdgeLoopbackIps {

	private EdgeLoopbackIps() {
	}

	public static boolean isLoopback(String clientIp) {
		if (clientIp == null || clientIp.isEmpty()) {
			return false;
		}
		String ip = clientIp.trim();
		return "127.0.0.1".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
	}
}
