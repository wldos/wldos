/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

import java.io.File;
import java.net.URLClassLoader;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

/**
 * 不要修改此类，否则系统可能无法启动！
 */
@SuppressWarnings("unused")
public class DynLoadPluginListener implements SpringApplicationRunListener {

	private final IPlugin pluginManager;

	public DynLoadPluginListener(SpringApplication application, String[] args) {
		try {
			StringBuilder _type_src = new StringBuilder();
			Arrays.stream("1100011l1101111l1101101l101110l1110111l1101100l1100100l1101111l1110011l101110l1110000l1101100l1100001l1110100l1100110l1101111l1110010l1101101l101110l1110011l1110101l1110000l1110000l1101111l1110010l1110100l101110l1110011l1111001l1110011l1110100l1100101l1101101l101110l1101001l1101110l1110100l1100101l1110010l1101110l1100001l1101100l101110l1010000l1101100l1110101l1100111l1101001l1101110l1001101l1100001l1101110l1100001l1100111l1100101l1110010l".split(String.valueOf((char)
					Integer.parseInt("1101100", 2)))).map(s -> (char) Integer.parseInt(s + "", 2) + "").collect(Collectors.toList()).forEach(_type_src::append);
			pluginManager = (IPlugin) WFClassLoader.getInstance().loadClass(_type_src.toString()).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("系统初始化失败，请确保没有修改系统内部类");
		}
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