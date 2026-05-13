/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import com.wldos.framework.schedule.GatedTaskScheduler;
import com.wldos.framework.schedule.SchedulingStartupGate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 调度配置：激活 {@code @Scheduled}，并通过 {@link GatedTaskScheduler}
 * 在 {@link org.springframework.boot.context.event.ApplicationReadyEvent} 到达前不执行已触发任务。
 * 测试等特殊场景可设置 {@code wldos.scheduling.skip-startup-gate=true} 关闭门禁。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @date 2026/2/1
 */
@Configuration
@EnableScheduling
public class SchedulingConfiguration {

    @Value("${scheduler.pool-size:#{T(java.lang.Math).max(T(java.lang.Runtime).getRuntime().availableProcessors() * 2, T(java.lang.Runtime).getRuntime().availableProcessors() + 1)}}")
    private int poolSize;

    @Value("${scheduler.await-termination-seconds:30}")
    private int awaitTerminationSeconds;

    @Value("${scheduler.thread-name-prefix:scheduler-}")
    private String threadNamePrefix;    

    @Bean
    public TaskScheduler taskScheduler(SchedulingStartupGate schedulingStartupGate) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(threadNamePrefix);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        scheduler.initialize();
        return new GatedTaskScheduler(scheduler, schedulingStartupGate);
    }
}
