/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.concurrent.ConcurrentHashMap;

import com.wldos.gateway.edge.EdgeIpPolicyMatch;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 无 token 访问需鉴权路径时的 per-IP 限流（{@link EdgeGateWayFilter} 调用）。
 *
 * <p>白名单 {@code gateway.ignore.path}、静态 {@code /store}、{@code /internal/mesh} 不经过此处；
 * 请求头带 token（含无效 token）不计入本限流。超限返回 429，不写 ACCESS_DENIED。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class EdgeGatewayIpRateLimiter {

	private static final long SECOND_MS = 1_000L;
	private static final long MINUTE_MS = 60_000L;

	private final Environment environment;

	private final ConcurrentHashMap<String, RateLimitEntry> entries = new ConcurrentHashMap<>(512);

	public EdgeGatewayIpRateLimiter(Environment environment) {
		this.environment = environment;
	}

	public boolean isEnabled() {
		return environment.getProperty("wldos.gateway.rate-limit.enabled", Boolean.class, Boolean.FALSE);
	}

	public boolean minimalBody() {
		return environment.getProperty("wldos.gateway.rate-limit.minimal-body", Boolean.class, Boolean.TRUE);
	}

	public boolean skipLoopback() {
		return environment.getProperty("wldos.gateway.rate-limit.skip-loopback", Boolean.class, Boolean.TRUE);
	}

	public int perSecondLimit() {
		int v = environment.getProperty("wldos.gateway.rate-limit.per-second", Integer.class, 20);
		return v > 0 ? v : 0;
	}

	public int perMinuteLimit() {
		int v = environment.getProperty("wldos.gateway.rate-limit.per-minute", Integer.class, 300);
		return v > 0 ? v : 300;
	}

	public int maxTrackedIps() {
		int v = environment.getProperty("wldos.gateway.rate-limit.max-ips", Integer.class, 10_000);
		return v > 0 ? v : 10_000;
	}

	/**
	 * 无 token 请求专用；与 Nginx {@code limit_req} 类似：短窗口挡扫射、长窗口挡持续滥用。
	 *
	 * @return {@code true} 允许继续（返回 401 等）；{@code false} 应直接 429
	 */
	public boolean tryAcquireNoToken(String clientIp) {
		return tryAcquireNoToken(clientIp, null);
	}

	/**
	 * @param rateOverride 来自 edge IP 策略 Hook 的 RATE_STRICT 规则，可空
	 */
	public boolean tryAcquireNoToken(String clientIp, EdgeIpPolicyMatch rateOverride) {
		if (!isEnabled()) {
			return true;
		}
		if (clientIp == null || clientIp.isEmpty()) {
			clientIp = "0.0.0.0";
		}
		if (skipLoopback() && EdgeLoopbackIps.isLoopback(clientIp)) {
			return true;
		}
		if (entries.size() > maxTrackedIps()) {
			entries.clear();
		}
		int secCap = resolvePerSecond(rateOverride);
		int minCap = resolvePerMinute(rateOverride);
		long now = System.currentTimeMillis();
		RateLimitEntry entry = entries.computeIfAbsent(clientIp, k -> new RateLimitEntry(now));
		synchronized (entry) {
			entry.rollWindows(now);
			entry.secondCount++;
			entry.minuteCount++;
			if (secCap > 0 && entry.secondCount > secCap) {
				return false;
			}
			return entry.minuteCount <= minCap;
		}
	}

	private int resolvePerSecond(EdgeIpPolicyMatch rateOverride) {
		if (rateOverride != null && rateOverride.getRatePerSecond() != null && rateOverride.getRatePerSecond() > 0) {
			return rateOverride.getRatePerSecond();
		}
		if (rateOverride != null) {
			int base = perSecondLimit();
			return base > 1 ? Math.max(1, base / 2) : base;
		}
		return perSecondLimit();
	}

	private int resolvePerMinute(EdgeIpPolicyMatch rateOverride) {
		if (rateOverride != null && rateOverride.getRatePerMinute() != null && rateOverride.getRatePerMinute() > 0) {
			return rateOverride.getRatePerMinute();
		}
		if (rateOverride != null) {
			int base = perMinuteLimit();
			return base > 2 ? Math.max(2, base / 2) : base;
		}
		return perMinuteLimit();
	}

	private static final class RateLimitEntry {
		volatile long secondWindowStartMs;
		volatile long minuteWindowStartMs;
		int secondCount;
		int minuteCount;

		RateLimitEntry(long now) {
			this.secondWindowStartMs = now;
			this.minuteWindowStartMs = now;
		}

		void rollWindows(long now) {
			if (now - secondWindowStartMs >= SECOND_MS) {
				secondWindowStartMs = now;
				secondCount = 0;
			}
			if (now - minuteWindowStartMs >= MINUTE_MS) {
				minuteWindowStartMs = now;
				minuteCount = 0;
			}
		}
	}
}
