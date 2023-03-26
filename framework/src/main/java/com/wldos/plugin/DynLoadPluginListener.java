/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.plugin;

import java.net.URLClassLoader;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SuppressWarnings("unused")
public class DynLoadPluginListener implements SpringApplicationRunListener {

	private final PluginManager pluginManager;

	public DynLoadPluginListener(SpringApplication application, String[] args) {
		pluginManager = new PluginManager();
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment configurableEnvironment) {
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