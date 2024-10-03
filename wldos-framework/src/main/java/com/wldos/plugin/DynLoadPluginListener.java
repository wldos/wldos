/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.plugin;

import java.io.File;
import java.net.URLClassLoader;
import java.time.Duration;
import java.util.Objects;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SuppressWarnings("unused")
public class DynLoadPluginListener implements SpringApplicationRunListener {

	private final PluginManager pluginManager;

	public DynLoadPluginListener(SpringApplication application, String[] args) {
		pluginManager = PluginManager.newInstance();
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment env) {
		pluginManager.setClassLoader((URLClassLoader) env.getClass().getClassLoader(), env);
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext configurableApplicationContext) {
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		String root;
		String inf;
		try {
			ClassLoader classLoader = getClass().getClassLoader();

			File webINF = new File(Objects.requireNonNull(classLoader.getResource("")).getFile()).getParentFile();
			root = webINF.getParentFile().getAbsolutePath();
			inf = webINF.getAbsolutePath();
		} catch (Exception e) {
			try { // for macOs
				Resource resource = context.getResource("classpath:wldos");
				File webINF = resource.getFile().getParentFile().getParentFile();
				root = webINF.getParentFile().getAbsolutePath();
				inf = webINF.getAbsolutePath();
			} catch (Exception ignored) {
				root = "";
				inf = "";
			}
		}
		System.setProperty("wldos.platform.root", root);
		System.setProperty("wldos.platform.web-inf", inf);
		pluginManager.register(context);
	}

	@Override
	public void started(ConfigurableApplicationContext context, Duration timeTaken) {
		pluginManager.boot(context);


	}

	@Override
	public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
	}
}