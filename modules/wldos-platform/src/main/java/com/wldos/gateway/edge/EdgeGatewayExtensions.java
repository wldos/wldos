/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway.edge;

/**
 * 网关 edge 的 WSHook 扩展点编码（不占用 sdk-api {@code ExtensionPoint}，便于与中央仓库 sdk 版本解耦）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class EdgeGatewayExtensions {

	public static final String IP_RESOLVE_POLICY = "edge.ip.resolvePolicy";

	public static final String REJECTION_ENRICH = "edge.rejection.enrich";

	public static final String PROBE_ON_HIT = "edge.probe.onHit";

	private EdgeGatewayExtensions() {
	}
}
