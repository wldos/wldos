/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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

import javax.servlet.ServletContext;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.env.MapPropertySource;
import java.util.Collections;

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
		String root = "";
		String inf = "";
		try {
			// 1. 优先用 ServletContext 获取 webroot
			ServletContext servletContext = null;
			try {
				servletContext = context.getBean(ServletContext.class);
			} catch (Exception ignored) {}
			if (servletContext != null) {
				String realPath = servletContext.getRealPath("/");
				if (realPath != null) {
					File webRootDir = new File(realPath);
					root = webRootDir.getAbsolutePath();
					File webInfDir = new File(webRootDir, "WEB-INF");
					if (webInfDir.exists()) {
						inf = webInfDir.getAbsolutePath();
					}
				}
			}
			// 2. 如果获取不到，再用 classLoader 方式
			if (root.isEmpty() || inf.isEmpty()) {
				File classPath = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("")).getFile());
				File tempClassPathFile = classPath;
				while (tempClassPathFile != null && !tempClassPathFile.getName().equalsIgnoreCase("WEB-INF") && !tempClassPathFile.getName().equalsIgnoreCase("target")) {
					tempClassPathFile = tempClassPathFile.getParentFile();
				}
				if (tempClassPathFile != null) {
					if (tempClassPathFile.getName().equalsIgnoreCase("WEB-INF")) {
						inf = tempClassPathFile.getAbsolutePath();
						root = tempClassPathFile.getParentFile().getAbsolutePath();
					} else if (tempClassPathFile.getName().equalsIgnoreCase("target")) {
						root = tempClassPathFile.getAbsolutePath();
						inf = new File(tempClassPathFile, "WEB-INF").getAbsolutePath();
					}
				}
			}
			// 3. MacOS 特殊处理（兼容原有逻辑）
			if ((root.isEmpty() || inf.isEmpty()) && context != null) {
				try {
					org.springframework.core.io.Resource resource = context.getResource("classpath:wldos");
					File webINF = resource.getFile().getParentFile().getParentFile();
					root = webINF.getParentFile().getAbsolutePath();
					inf = webINF.getAbsolutePath();
				} catch (Exception ignored) {}
			}
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

		ConfigurableEnvironment env = context.getEnvironment();
		env.getPropertySources().addFirst(
			new MapPropertySource("wldos-platform-root",
				Collections.singletonMap("wldos.platform.root", root))
		);

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