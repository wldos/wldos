/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 调度配置：激活 @Scheduled 定时任务
 *
 * @author 元悉宇宙
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
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(threadNamePrefix);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        scheduler.initialize();
        return scheduler;
    }
}
