/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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

import io.github.wldos.framework.support.audit.IOpLogger;
import io.github.wldos.framework.support.audit.OpEvent;
import com.wldos.framework.support.audit.port.OpLogPersistPort;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志默认实现：异步队列 + 周期/批量落盘 + 全链路降级。
 *
 * <p>设计与 {@link DefaultLoginLogger} 同款；操作日志 TPS 通常远高于登录，
 * 因此队列容量更大（{@value #QUEUE_CAPACITY}）。
 *
 * <p>降级策略与登录日志一致：队列满 / 端口缺失 / 入库异常 → logback warn，不阻塞业务。
 *
 * <p>Bean 装配：由 {@code com.wldos.framework.support.audit.config.AuditAutoConfiguration}
 * 通过 {@code @Bean + @ConditionalOnMissingBean(IOpLogger.class)} 装配，理由参见 {@link DefaultLoginLogger}。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
public class DefaultOpLogger implements IOpLogger {

	private static final int QUEUE_CAPACITY = 10000;

	private static final int BATCH_SIZE = 200;

	private static final long DRAIN_INTERVAL_MS = 1000L;

	private final BlockingQueue<OpEvent> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

	private final AtomicBoolean running = new AtomicBoolean(false);

	private Thread consumer;

	@Autowired(required = false)
	private OpLogPersistPort persistPort;

	@PostConstruct
	public void start() {
		if (!running.compareAndSet(false, true)) {
			return;
		}
		consumer = new Thread(this::consumeLoop, "wldos-op-log-consumer");
		consumer.setDaemon(true);
		consumer.start();
		if (persistPort == null) {
			log.warn("[op.log] OpLogPersistPort not present, all events fallback to logback only");
		}
		else {
			log.info("[op.log] DefaultOpLogger started, queue={} batch={} drainInterval={}ms",
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
	public void recordAsync(OpEvent event) {
		if (event == null) {
			return;
		}
		if (!queue.offer(event)) {
			fallback(event, null);
		}
	}

	private void consumeLoop() {
		while (running.get() || !queue.isEmpty()) {
			try {
				OpEvent first = queue.poll(DRAIN_INTERVAL_MS, TimeUnit.MILLISECONDS);
				if (first == null) {
					continue;
				}
				List<OpEvent> batch = new ArrayList<>(BATCH_SIZE);
				batch.add(first);
				queue.drainTo(batch, BATCH_SIZE - 1);
				flush(batch);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			catch (Throwable t) {
				log.warn("[op.log] consume loop error, will continue", t);
			}
		}
	}

	private void flushRemaining() {
		if (queue.isEmpty()) {
			return;
		}
		List<OpEvent> rest = new ArrayList<>(queue.size());
		queue.drainTo(rest);
		flush(rest);
	}

	private void flush(List<OpEvent> batch) {
		if (batch == null || batch.isEmpty()) {
			return;
		}
		if (persistPort == null) {
			for (OpEvent e : batch) {
				fallback(e, null);
			}
			return;
		}
		try {
			persistPort.saveBatch(batch);
		}
		catch (Throwable t) {
			log.warn("[op.log] saveBatch failed, fallback to per-record (size={})", batch.size(), t);
			for (OpEvent e : batch) {
				try {
					persistPort.saveOne(e);
				}
				catch (Throwable t2) {
					fallback(e, t2);
				}
			}
		}
	}

	private static void fallback(OpEvent event, Throwable cause) {
		if (cause == null) {
			log.warn("[op.log.fallback] module={} action={} user={}",
					event == null ? null : event.getModule(),
					event == null ? null : event.getAction(),
					event == null ? null : event.getUserId());
		}
		else {
			log.warn("[op.log.fallback] module={} action={} user={} cause={}",
					event == null ? null : event.getModule(),
					event == null ? null : event.getAction(),
					event == null ? null : event.getUserId(),
					cause.getMessage());
		}
	}
}
