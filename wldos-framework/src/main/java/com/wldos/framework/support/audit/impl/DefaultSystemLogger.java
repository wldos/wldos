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

import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import com.wldos.framework.support.audit.port.SystemLogPersistPort;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志默认实现：异步队列 + 周期/批量落盘 + 全链路降级。
 *
 * <p>设计与 {@link DefaultLoginLogger}/{@link DefaultOpLogger} 同款；系统事件 TPS 通常很低
 * （配置变更、调度任务、关键异常等），所以队列容量较小。
 *
 * <p>Bean 装配：由 {@code com.wldos.framework.support.audit.config.AuditAutoConfiguration}
 * 通过 {@code @Bean + @ConditionalOnMissingBean(ISystemLogger.class)} 装配，理由参见 {@link DefaultLoginLogger}。
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Slf4j
public class DefaultSystemLogger implements ISystemLogger {

	private static final int QUEUE_CAPACITY = 2048;

	private static final int BATCH_SIZE = 50;

	private static final long DRAIN_INTERVAL_MS = 1000L;

	private final BlockingQueue<SystemEvent> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

	private final AtomicBoolean running = new AtomicBoolean(false);

	private Thread consumer;

	@Autowired(required = false)
	private SystemLogPersistPort persistPort;

	@PostConstruct
	public void start() {
		if (!running.compareAndSet(false, true)) return;
		consumer = new Thread(this::consumeLoop, "wldos-sys-log-consumer");
		consumer.setDaemon(true);
		consumer.start();
		if (persistPort == null) {
			log.warn("[sys.log] SystemLogPersistPort not present, all events fallback to logback only");
		}
		else {
			log.info("[sys.log] DefaultSystemLogger started, queue={} batch={} drainInterval={}ms",
					QUEUE_CAPACITY, BATCH_SIZE, DRAIN_INTERVAL_MS);
		}
	}

	@PreDestroy
	public void stop() {
		if (!running.compareAndSet(true, false)) return;
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
	public void recordAsync(SystemEvent event) {
		if (event == null) return;
		if (!queue.offer(event)) {
			fallback(event, null);
		}
	}

	private void consumeLoop() {
		while (running.get() || !queue.isEmpty()) {
			try {
				SystemEvent first = queue.poll(DRAIN_INTERVAL_MS, TimeUnit.MILLISECONDS);
				if (first == null) continue;
				List<SystemEvent> batch = new ArrayList<>(BATCH_SIZE);
				batch.add(first);
				queue.drainTo(batch, BATCH_SIZE - 1);
				flush(batch);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			catch (Throwable t) {
				log.warn("[sys.log] consume loop error, will continue", t);
			}
		}
	}

	private void flushRemaining() {
		if (queue.isEmpty()) return;
		List<SystemEvent> rest = new ArrayList<>(queue.size());
		queue.drainTo(rest);
		flush(rest);
	}

	private void flush(List<SystemEvent> batch) {
		if (batch == null || batch.isEmpty()) return;
		if (persistPort == null) {
			for (SystemEvent e : batch) fallback(e, null);
			return;
		}
		try {
			persistPort.saveBatch(batch);
		}
		catch (Throwable t) {
			log.warn("[sys.log] saveBatch failed, fallback to per-record (size={})", batch.size(), t);
			for (SystemEvent e : batch) {
				try { persistPort.saveOne(e); }
				catch (Throwable t2) { fallback(e, t2); }
			}
		}
	}

	private static void fallback(SystemEvent event, Throwable cause) {
		if (event == null) return;
		String prefix = event.getSeverity() == null ? "INFO" : event.getSeverity().name();
		if (cause == null) {
			log.warn("[sys.log.fallback][{}] {} / {} : {}", prefix, event.getSourceModule(), event.getEventType(), event.getMessage());
		}
		else {
			log.warn("[sys.log.fallback][{}] {} / {} : {} cause={}", prefix, event.getSourceModule(), event.getEventType(), event.getMessage(), cause.getMessage());
		}
	}
}
