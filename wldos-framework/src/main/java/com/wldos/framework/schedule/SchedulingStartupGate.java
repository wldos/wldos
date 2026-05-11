/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.schedule;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 全局调度门禁：仅在应用完成启动（{@link ApplicationReadyEvent}）之后，
 * 由 {@link GatedTaskScheduler} 放行的 {@link org.springframework.scheduling.annotation.Scheduled} 任务才会执行。
 * <p>
 * 自建线程池的定时逻辑可注入本类，在任务入口调用  或根据 {@link #isApplicationReady()} 短路。
 *
 * @author Yuanxi Universe
 */
@Component
public class SchedulingStartupGate {

	private final AtomicBoolean applicationReady = new AtomicBoolean(false);

	@Value("${wldos.scheduling.skip-startup-gate:false}")
	private boolean skipStartupGate;

	public boolean isApplicationReady() {
		return skipStartupGate || applicationReady.get();
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(0)
	public void onApplicationReady() {
		applicationReady.set(true);
	}

	/**
	 * 未就绪则直接返回，供非 Spring TaskScheduler 的调度代码复用。
	 */
	public void requireReady(Runnable task) {
		if (isApplicationReady()) {
			task.run();
		}
	}
}
