/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.framework.support.audit.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import io.github.wldos.framework.support.audit.ILoginLogger;
import io.github.wldos.framework.support.audit.LoginEvent;
import com.wldos.framework.support.audit.port.LoginLogPersistPort;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录日志默认实现：异步队列 + 周期/批量落盘 + 全链路降级。
 *
 * <p>容量与性能默认值（硬编码，简单优先；后续如需配置化再升级 properties）：
 * <ul>
 *   <li>队列容量：{@value #QUEUE_CAPACITY}（约 1MB，单条 LoginEvent ~256B）；</li>
 *   <li>批大小：{@value #BATCH_SIZE}（一次最多消费 N 条入库）；</li>
 *   <li>消费周期：{@value #DRAIN_INTERVAL_MS} ms（攒批超时上限，防止低频时单条压久）。</li>
 * </ul>
 *
 * <p>降级策略（按优先级）：
 * <ol>
 *   <li><b>队列满</b>：{@code recordAsync} 直接返回 false 路径 → 仅 logback 警告，不阻塞业务；</li>
 *   <li><b>{@code LoginLogPersistPort} 未注入</b>（如裁剪版）：每条事件 logback warn，不入库；</li>
 *   <li><b>持久化抛异常</b>：捕获 + logback warn，<b>不</b>把 batch 退回队列（避免雪崩）。</li>
 * </ol>
 *
 * <p>Bean 装配：由 {@code com.wldos.framework.support.audit.config.AuditAutoConfiguration}
 * 通过 {@code @Bean + @ConditionalOnMissingBean(ILoginLogger.class)} 装配。
 * 增强或插件提供另一个 {@link ILoginLogger} {@code @Bean} 即可覆盖默认实现，业务侧调用代码不变。
 *
 * <p><b>注意</b>：本类<b>不再</b>使用 {@code @Component} + {@code @ConditionalOnMissingBean}，
 * 因为 Spring Boot 官方仅在 {@code @Configuration} 的 {@code @Bean} 方法上保证条件评估顺序稳定。
 *
 * @author Yuanxi Universe
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
public class DefaultLoginLogger implements ILoginLogger {

	/** 队列最大长度。 */
	private static final int QUEUE_CAPACITY = 4096;

	/** 单次消费最多取多少条入库。 */
	private static final int BATCH_SIZE = 100;

	/** 周期消费间隔（毫秒）。 */
	private static final long DRAIN_INTERVAL_MS = 1000L;

	private final BlockingQueue<LoginEvent> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

	private final AtomicBoolean running = new AtomicBoolean(false);

	private Thread consumer;

	@Autowired(required = false)
	private LoginLogPersistPort persistPort;

	@PostConstruct
	public void start() {
		if (!running.compareAndSet(false, true)) {
			return;
		}
		consumer = new Thread(this::consumeLoop, "wldos-login-log-consumer");
		consumer.setDaemon(true);
		consumer.start();
		if (persistPort == null) {
			log.warn("[login.log] LoginLogPersistPort not present, all events fallback to logback only");
		}
		else {
			log.info("[login.log] DefaultLoginLogger started, queue={} batch={} drainInterval={}ms",
					QUEUE_CAPACITY, BATCH_SIZE, DRAIN_INTERVAL_MS);
		}
	}

	@PreDestroy
	public void stop() {
		if (!running.compareAndSet(true, false)) {
			return;
		}
		if (consumer != null) {
			consumer.interrupt();
			try {
				consumer.join(3000);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		flushRemaining();
	}

	@Override
	public void record(LoginEvent event) {
		if (event == null) {
			return;
		}
		if (persistPort == null) {
			fallback(event, null);
			return;
		}
		try {
			persistPort.saveOne(event);
		}
		catch (Throwable t) {
			fallback(event, t);
		}
	}

	@Override
	public void recordAsync(LoginEvent event) {
		if (event == null) {
			return;
		}
		if (!queue.offer(event)) {
			// 队列已满：降级 logback。日志丢一条远比阻塞登录请求好。
			fallback(event, null);
		}
	}

	/* ========== 内部消费循环 ========== */

	private void consumeLoop() {
		while (running.get() || !queue.isEmpty()) {
			try {
				LoginEvent first = queue.poll(DRAIN_INTERVAL_MS, TimeUnit.MILLISECONDS);
				if (first == null) {
					continue;
				}
				List<LoginEvent> batch = new ArrayList<>(BATCH_SIZE);
				batch.add(first);
				queue.drainTo(batch, BATCH_SIZE - 1);
				flush(batch);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			catch (Throwable t) {
				log.warn("[login.log] consume loop error, will continue", t);
			}
		}
	}

	private void flushRemaining() {
		if (queue.isEmpty()) {
			return;
		}
		List<LoginEvent> rest = new ArrayList<>(queue.size());
		queue.drainTo(rest);
		flush(rest);
	}

	private void flush(List<LoginEvent> batch) {
		if (batch == null || batch.isEmpty()) {
			return;
		}
		if (persistPort == null) {
			for (LoginEvent e : batch) {
				fallback(e, null);
			}
			return;
		}
		try {
			persistPort.saveBatch(batch);
		}
		catch (Throwable t) {
			// 批量失败：退回单条尝试，最大化保留率；仍然失败则 logback 兜底
			log.warn("[login.log] saveBatch failed, fallback to per-record (size={})", batch.size(), t);
			for (LoginEvent e : batch) {
				try {
					persistPort.saveOne(e);
				}
				catch (Throwable t2) {
					fallback(e, t2);
				}
			}
		}
	}

	private static void fallback(LoginEvent event, Throwable cause) {
		if (cause == null) {
			log.warn("[login.log.fallback] {}", event);
		}
		else {
			log.warn("[login.log.fallback] {} cause={}", event, cause.getMessage());
		}
	}
}
