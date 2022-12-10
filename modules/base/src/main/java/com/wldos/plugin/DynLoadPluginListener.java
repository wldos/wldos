/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 运行时控制spring容器，动态加载、热插拔bean。
 * 还需要进一步实现。隐藏代码记录，见git历史！！！
 */
@SuppressWarnings("unused")
public abstract class DynLoadPluginListener implements SpringApplicationRunListener {

    private final PluginManager pluginManager;

    public DynLoadPluginListener(SpringApplication application, String[] args) {
        pluginManager = new PluginManager();
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext configurableApplicationContext) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext configurableApplicationContext) {
        pluginManager.register(configurableApplicationContext);
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        pluginManager.boot(context);
    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }
}