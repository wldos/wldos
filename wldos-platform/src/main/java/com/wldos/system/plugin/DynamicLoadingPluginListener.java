/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 运行时控制spring容器，动态加载、热插拔bean。
 */
@SuppressWarnings("unused")
public abstract class DynamicLoadingPluginListener implements SpringApplicationRunListener {

    private final PluginManager pluginManager;

    public DynamicLoadingPluginListener(SpringApplication application, String[] args) {
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
