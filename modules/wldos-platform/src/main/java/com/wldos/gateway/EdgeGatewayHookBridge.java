/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.List;

import io.github.wldos.framework.support.hook.WSHook;
import com.wldos.gateway.edge.EdgeGatewayExtensions;
import com.wldos.gateway.edge.EdgeIpPolicyMatch;

import org.springframework.stereotype.Component;

/**
 * 网关 edge 扩展点门面；商业能力经 Hook 注册，社区版无 Handler 时为 no-op。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class EdgeGatewayHookBridge {

	private final WSHook wsHook;

	public EdgeGatewayHookBridge(WSHook wsHook) {
		this.wsHook = wsHook;
	}

	public EdgeIpPolicyMatch resolveIpPolicy(String clientIp) {
		if (clientIp == null || clientIp.isEmpty()) {
			return null;
		}
		try {
			Object out = this.wsHook.applyHandler(EdgeGatewayExtensions.IP_RESOLVE_POLICY, clientIp);
			if (out instanceof EdgeIpPolicyMatch) {
				return (EdgeIpPolicyMatch) out;
			}
		}
		catch (Exception ignored) {
			// 旁路
		}
		return null;
	}

	public void enrichRejection(EdgeRejectionContext ctx, String eventType, String message,
			List<String> probePrefixes) {
		if (ctx == null) {
			return;
		}
		try {
			this.wsHook.applyHandler(EdgeGatewayExtensions.REJECTION_ENRICH, ctx, eventType, message,
					probePrefixes);
		}
		catch (Exception ignored) {
			// 旁路
		}
	}

	public boolean shouldAuditProbeHit(EdgeRejectionContext ctx, String reqUri) {
		try {
			Object out = this.wsHook.applyHandler(EdgeGatewayExtensions.PROBE_ON_HIT, ctx, reqUri);
			if (out instanceof Boolean) {
				return (Boolean) out;
			}
		}
		catch (Exception ignored) {
			// 旁路
		}
		return false;
	}
}
