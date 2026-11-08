/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 按 IP 滑动窗口统计两类行为频次，供 edge 分类识别单次请求看不出的攻击模式：路径分析只能抓到"猜路径"的
 * 攻击，真正的撞库/爆破/越权探测打的是应用真实存在的接口，只有频次能看出来——正常用户 token 失效顶多
 * 重试几次，短时间内大量同类失败才是异常。结构与 {@link EdgeGatewayIpRateLimiter} 一致。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class EdgeGatewayAuthAnomalyTracker {

	private final Environment environment;

	private final ConcurrentHashMap<String, Window> nonexistentRouteHits = new ConcurrentHashMap<>(512);

	private final ConcurrentHashMap<String, Window> realEndpointAuthFailures = new ConcurrentHashMap<>(512);

	public EdgeGatewayAuthAnomalyTracker(Environment environment) {
		this.environment = environment;
	}

	/** 记录一次"探测不存在路径"，返回该 IP 当前窗口内累计次数。 */
	public int recordNonexistentRouteHit(String ip) {
		return record(nonexistentRouteHits, ip);
	}

	/** 记录一次"对真实接口的认证失败"，返回该 IP 当前窗口内累计次数。 */
	public int recordRealEndpointAuthFailure(String ip) {
		return record(realEndpointAuthFailures, ip);
	}

	public boolean isNonexistentRouteVolumeExceeded(int windowCount) {
		return windowCount >= nonexistentRouteThreshold();
	}

	public boolean isAuthFailureVolumeExceeded(int windowCount) {
		return windowCount >= authFailureThreshold();
	}

	private int record(ConcurrentHashMap<String, Window> map, String ip) {
		String key = (ip == null || ip.isEmpty()) ? "0.0.0.0" : ip;
		if (map.size() > maxTrackedIps()) {
			map.clear();
		}
		long now = System.currentTimeMillis();
		Window window = map.computeIfAbsent(key, k -> new Window(now));
		synchronized (window) {
			if (now - window.windowStartMs >= windowMs()) {
				window.windowStartMs = now;
				window.count = 0;
			}
			window.count++;
			return window.count;
		}
	}

	private long windowMs() {
		int minutes = environment.getProperty("wldos.gateway.anomaly.window-minutes", Integer.class, 5);
		return (minutes > 0 ? minutes : 5) * 60_000L;
	}

	private int nonexistentRouteThreshold() {
		int v = environment.getProperty("wldos.gateway.anomaly.nonexistent-route-threshold", Integer.class, 5);
		return v > 0 ? v : 5;
	}

	private int authFailureThreshold() {
		int v = environment.getProperty("wldos.gateway.anomaly.auth-failure-threshold", Integer.class, 10);
		return v > 0 ? v : 10;
	}

	private int maxTrackedIps() {
		int v = environment.getProperty("wldos.gateway.anomaly.max-ips", Integer.class, 10_000);
		return v > 0 ? v : 10_000;
	}

	private static final class Window {
		volatile long windowStartMs;
		int count;

		Window(long now) {
			this.windowStartMs = now;
		}
	}
}
