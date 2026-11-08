/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import io.github.wldos.framework.support.audit.SystemEventSeverity;

import com.wldos.platform.audit.support.SystemLogContextEnricher;
import com.wldos.platform.audit.support.SystemLogContextEnricher.TenantStamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 网关 edge 拒绝类系统日志聚合写库，降低 wo_sys_log 扫描噪声。
 *
 * <p>来源分类（{@code actorKind}）在聚合桶 flush 时才计算（{@link AggregateBucket#toEvent()}），不在
 * 每个被拒绝请求进入时同步算：分类含路由真值查询等有实际计算量的判断，聚合窗口内重复请求本来就只有
 * 首条的分类结果会被保留，提前对每条都算一次纯属浪费；挪到 flush 时，一个窗口内 N 次重复扫描只算 1 次，
 * 且 flush 跑在 {@link #flushExpiredWindows()} 的调度线程上，不占用请求处理线程。{@code MODE_EACH}
 * （非聚合，低流量调试场景）不经过这个收益路径，在 {@link #submit} 内同步分类，足够简单也没必要更复杂。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class EdgeAccessDeniedAggregator {

	static final String MODE_EACH = "each";
	static final String MODE_AGGREGATE = "aggregate";
	static final String MODE_OFF = "off";

	private static final String PROP_MODE = "wldos.gateway.audit.access-denied.mode";
	private static final String PROP_WINDOW = "wldos.gateway.audit.access-denied.window-seconds";
	private static final String PROP_FLUSH_COUNT = "wldos.gateway.audit.access-denied.flush-count-threshold";
	private static final String PROP_MAX_BUCKETS = "wldos.gateway.audit.access-denied.max-buckets";

	private final Environment environment;

	private final ConcurrentHashMap<String, AggregateBucket> buckets = new ConcurrentHashMap<>(256);

	@Autowired(required = false)
	private ISystemLogger systemLogger;

	/** 分类 Hook 桥接；社区版无商业分类 Handler 时为 no-op，ctx.actorKind 保持空。 */
	@Autowired(required = false)
	private EdgeGatewayHookBridge edgeHookBridge;

	@Autowired(required = false)
	private EdgeGatewayProbeMatcher probeMatcher;

	private final SystemLogContextEnricher systemLogContextEnricher;

	public EdgeAccessDeniedAggregator(Environment environment, SystemLogContextEnricher systemLogContextEnricher) {
		this.environment = environment;
		this.systemLogContextEnricher = systemLogContextEnricher;
	}

	public void submit(ISystemLogger logger, SystemEvent event, EdgeRejectionContext ctx) {
		ISystemLogger target = logger != null ? logger : this.systemLogger;
		if (target == null || event == null) {
			return;
		}
		if ("ACCESS_DENIED".equals(event.getEventType()) && MODE_OFF.equalsIgnoreCase(auditMode())) {
			return;
		}
		if (MODE_EACH.equalsIgnoreCase(auditMode())) {
			classify(ctx, event.getEventType(), event.getMessage());
			event.withMetadata(EdgeRejectionMetadataBuilder.build(ctx));
			target.recordAsync(event);
			return;
		}
		if (buckets.size() > maxBuckets()) {
			buckets.clear();
		}
		String key = aggregateKey(event.getSourceModule(), event.getEventType(), event.getMessage(), ctx.getClientIp());
		long now = System.currentTimeMillis();
		AggregateBucket bucket = buckets.computeIfAbsent(key, k -> new AggregateBucket());
		SystemEvent flushNow = null;
		synchronized (bucket) {
			if (bucket.count > 0 && now - bucket.windowStartMs >= windowMs()) {
				flushNow = bucket.toEvent();
				bucket.reset(now, event, ctx);
			}
			else if (bucket.count == 0) {
				bucket.seed(event, ctx, now);
			}
			bucket.merge(ctx);
			if (bucket.count >= flushCountThreshold()) {
				flushNow = bucket.toEvent();
				bucket.reset(now, event, ctx);
			}
		}
		if (flushNow != null) {
			target.recordAsync(flushNow);
		}
	}

	@Scheduled(fixedDelayString = "${wldos.gateway.audit.access-denied.flush-interval-ms:60000}")
	public void flushExpiredWindows() {
		if (!MODE_AGGREGATE.equalsIgnoreCase(auditMode()) || systemLogger == null) {
			return;
		}
		long now = System.currentTimeMillis();
		long window = windowMs();
		for (Map.Entry<String, AggregateBucket> entry : buckets.entrySet()) {
			AggregateBucket bucket = entry.getValue();
			SystemEvent ev;
			synchronized (bucket) {
				if (bucket.count <= 0 || now - bucket.windowStartMs < window) {
					continue;
				}
				ev = bucket.toEvent();
				// 原地清空，不摘除 map entry：bucket 对象一旦为某个 key 创建就不再从 buckets 里摘除，
				// 避免并发 submit() 拿到即将/刚被摘除的孤儿对象、把新事件合并进一个再也不会被 flush 的桶
				// （竞态窗口：flush 释放锁到摘除 entry 之间，若恰好有 submit() 用同一 bucket 引用 merge
				// 了新事件，之后摘除会让这条事件永久丢失）。map 整体大小改由 submit() 里的 maxBuckets 兜底。
				bucket.clear();
			}
			if (ev != null) {
				systemLogger.recordAsync(ev);
			}
		}
	}

	/** 桶为空闲置代价很小（几个字段），定期整体容量兜底即可，不需要摘除单个 key 来省这点内存。 */
	private int maxBuckets() {
		int v = environment.getProperty(PROP_MAX_BUCKETS, Integer.class, 10_000);
		return v > 0 ? v : 10_000;
	}

	private void classify(EdgeRejectionContext ctx, String eventType, String message) {
		if (ctx == null || this.edgeHookBridge == null) {
			return;
		}
		List<String> prefixes = this.probeMatcher == null ? Collections.emptyList() : this.probeMatcher.probePrefixes();
		this.edgeHookBridge.enrichRejection(ctx, eventType, message, prefixes);
	}

	String auditMode() {
		return environment.getProperty(PROP_MODE, MODE_AGGREGATE);
	}

	long windowMs() {
		int sec = environment.getProperty(PROP_WINDOW, Integer.class, 300);
		return (sec > 0 ? sec : 300) * 1000L;
	}

	int flushCountThreshold() {
		int v = environment.getProperty(PROP_FLUSH_COUNT, Integer.class, 1000);
		return v > 0 ? v : 1000;
	}

	private static String aggregateKey(String module, String eventType, String message, String ip) {
		String msg = message == null ? "" : message;
		if (msg.length() > 80) {
			msg = msg.substring(0, 80);
		}
		return (module == null ? "edge" : module) + '|' + eventType + '|' + msg + '|' + (ip == null ? "" : ip);
	}

	/** 非 static：flush 时调用外层 {@link #classify} 做分类，不用额外传引用。 */
	final class AggregateBucket {
		volatile long windowStartMs;
		int count;
		String sourceModule;
		String eventType;
		SystemEventSeverity severity;
		String messageBase;
		String sampleUri;
		String lastUri;
		String method;
		String querySample;
		String clientIp;
		String userId;
		String host;
		String userAgent;
		String referer;
		Long stampDomainId;
		Long stampComId;
		Long stampOperatorUserId;

		void seed(SystemEvent event, EdgeRejectionContext ctx, long now) {
			windowStartMs = now;
			sourceModule = event.getSourceModule();
			eventType = event.getEventType();
			severity = event.getSeverity();
			messageBase = event.getMessage();
			applyCtx(ctx, true);
			count = 0;
		}

		void reset(long now, SystemEvent event, EdgeRejectionContext ctx) {
			windowStartMs = now;
			count = 0;
			sourceModule = event.getSourceModule();
			eventType = event.getEventType();
			severity = event.getSeverity();
			messageBase = event.getMessage();
			sampleUri = null;
			lastUri = null;
			method = null;
			querySample = null;
			host = null;
			userAgent = null;
			referer = null;
			stampDomainId = null;
			stampComId = null;
			stampOperatorUserId = null;
			applyCtx(ctx, true);
		}

		void merge(EdgeRejectionContext ctx) {
			count++;
			applyCtx(ctx, false);
		}

		void applyCtx(EdgeRejectionContext ctx, boolean resetIdentityFields) {
			if (ctx == null) {
				return;
			}
			clientIp = ctx.getClientIp();
			userId = ctx.getUserId();
			if (ctx.getReqUri() != null) {
				if (sampleUri == null || sampleUri.isEmpty()) {
					sampleUri = ctx.getReqUri();
				}
				lastUri = ctx.getReqUri();
			}
			if (method == null && ctx.getMethod() != null) {
				method = ctx.getMethod();
			}
			if (querySample == null && ctx.getQuery() != null) {
				querySample = ctx.getQuery();
			}
			if (resetIdentityFields || host == null) {
				if (ctx.getHost() != null) {
					host = ctx.getHost();
				}
				if (ctx.getUserAgent() != null) {
					userAgent = ctx.getUserAgent();
				}
				if (ctx.getReferer() != null) {
					referer = ctx.getReferer();
				}
			}
			if (resetIdentityFields && stampDomainId == null) {
				TenantStamp stamp = EdgeAccessDeniedAggregator.this.systemLogContextEnricher.captureFromCurrentRequest();
				stampDomainId = stamp.getDomainId();
				stampComId = stamp.getComId();
				stampOperatorUserId = stamp.getOperatorUserId();
			}
			if (stampOperatorUserId == null && userId != null) {
				stampOperatorUserId = SystemLogContextEnricher.parseLongString(userId);
			}
		}

		void clear() {
			count = 0;
		}

		SystemEvent toEvent() {
			if (count <= 0) {
				return null;
			}
			// 分类挪到这里：flush 时对整个窗口只算一次，而不是 merge() 每次都算（含路由真值查询等
			// 有实际计算量的判断，聚合桶只保留一次分类结果，提前对每条重复请求都算纯属浪费）。
			EdgeRejectionContext ctx = new EdgeRejectionContext(clientIp, lastUri != null ? lastUri : sampleUri,
					userId, method, querySample);
			ctx.setHost(host);
			ctx.setUserAgent(userAgent);
			ctx.setReferer(referer);
			classify(ctx, eventType, messageBase);

			String msg = messageBase;
			if (count > 1) {
				msg = messageBase + " (聚合 count=" + count + ")";
			}
			SystemEvent ev;
			if (SystemEventSeverity.ERROR == severity) {
				ev = SystemEvent.error(sourceModule, eventType, msg);
			}
			else {
				ev = SystemEvent.warn(sourceModule, eventType, msg);
			}
			ev.withMetadata(EdgeRejectionMetadataBuilder.build(ctx, count, sampleUri));
			if (stampOperatorUserId != null) {
				ev.withOperator(stampOperatorUserId);
			}
			if (stampDomainId != null || stampComId != null) {
				ev.withTenant(stampDomainId, stampComId);
			}
			return ev;
		}
	}
}
