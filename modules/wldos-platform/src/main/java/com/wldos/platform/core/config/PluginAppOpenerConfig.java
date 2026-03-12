/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.config;

import com.wldos.platform.core.service.AppService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.wldos.platform.support.plugins.IPluginAppOpener;

/**
 * Platform 侧 IPluginAppOpener 配置。
 * 当不存在商业实现（Agent）时激活，委托 AppService 执行。
 *
 * @author 元悉宇宙
 */
@Configuration
public class PluginAppOpenerConfig {

    @Bean
    @ConditionalOnMissingBean(IPluginAppOpener.class)
    public IPluginAppOpener platformPluginAppOpener(AppService appService) {
        return (pluginCode, pluginName, appOrigin) -> appService.getOrCreatePluginApp(pluginCode, pluginName, appOrigin);
    }
}
