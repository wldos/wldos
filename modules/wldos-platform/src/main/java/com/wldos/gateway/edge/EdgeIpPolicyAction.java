/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway.edge;

/**
 * 网关 IP 策略动作（Hook {@link EdgeGatewayExtensions#IP_RESOLVE_POLICY} 返回值）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public enum EdgeIpPolicyAction {

	DENY,
	RATE_STRICT
}
