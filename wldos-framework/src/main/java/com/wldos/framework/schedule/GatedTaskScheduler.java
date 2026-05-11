/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.schedule;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 委托给 {@link ThreadPoolTaskScheduler}，在门禁未放开前不执行已提交的 {@link Runnable}
 * （{@code @Scheduled} 由 Spring 注册到本调度器，避免启动过程中访问尚未就绪的资源）。
 *
 * @author Yuanxi Universe
 */
public final class GatedTaskScheduler implements TaskScheduler, DisposableBean {

	private final ThreadPoolTaskScheduler delegate;
	private final SchedulingStartupGate gate;

	public GatedTaskScheduler(ThreadPoolTaskScheduler delegate, SchedulingStartupGate gate) {
		this.delegate = delegate;
		this.gate = gate;
	}

	private Runnable wrap(@Nullable Runnable task) {
		if (task == null) {
			return null;
		}
		return () -> {
			if (!gate.isApplicationReady()) {
				return;
			}
			task.run();
		};
	}

	@Override
	@Nullable
	public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
		return delegate.schedule(wrap(task), trigger);
	}

	@Override
	@Nullable
	public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
		return delegate.schedule(wrap(task), startTime);
	}

	@Override
	@Nullable
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
		return delegate.scheduleAtFixedRate(wrap(task), startTime, period);
	}

	@Override
	@Nullable
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
		return delegate.scheduleAtFixedRate(wrap(task), period);
	}

	@Override
	@Nullable
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
		return delegate.scheduleWithFixedDelay(wrap(task), startTime, delay);
	}

	@Override
	@Nullable
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
		return delegate.scheduleWithFixedDelay(wrap(task), delay);
	}

	@Override
	public void destroy() {
		delegate.destroy();
	}
}
