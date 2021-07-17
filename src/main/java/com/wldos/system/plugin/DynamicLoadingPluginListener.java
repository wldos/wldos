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
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.URLClassLoader;

/**
 * 运行时控制spring容器，动态加载、热插拔bean。
 * 还需要进一步实现。
 */
public class DynamicLoadingPluginListener implements SpringApplicationRunListener {

    private PluginManager pluginManager;

    public DynamicLoadingPluginListener(SpringApplication application, String[] args) {
        pluginManager = new PluginManager();
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment configurableEnvironment) {
        //JDBCManager.url = configurableEnvironment.getProperty("spring.datasource.url");
        //JDBCManager.username = configurableEnvironment.getProperty("spring.datasource.username");
        //JDBCManager.password = configurableEnvironment.getProperty("spring.datasource.password");

        pluginManager.setClassLoader((URLClassLoader) configurableEnvironment.getClass().getClassLoader());
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
