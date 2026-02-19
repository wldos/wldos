/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import com.wldos.common.Constants;
import com.wldos.framework.support.storage.utils.StoreUtils;
import java.security.CodeSource;
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Collections;

/**
 * 动态加载插件管理器监听器
 * 支持自动降级：如果 Agent 模块存在，使用 Agent 的高级实现；否则使用默认实现（开源版本）
 * 
 * @author 元悉宇宙
 * @version 2.0
 * @date 2026-01-10
 */
@SuppressWarnings("unused")
public class DynLoadPluginListener implements SpringApplicationRunListener {

	private final IPlugin pluginManager;
	private final boolean agentAvailable;

	public DynLoadPluginListener(SpringApplication application, String[] args) {
		IPlugin loadedManager = null;
		boolean agentFound = false;
		
		try {
			// 1. 尝试通过二进制编码解密类名
			StringBuilder _type_src = new StringBuilder();
			Arrays.stream("1100011l1101111l1101101l101110l1110111l1101100l1100100l1101111l1110011l101110l1110000l1101100l1100001l1110100l1100110l1101111l1110010l1101101l101110l1110011l1110101l1110000l1110000l1101111l1110010l1110100l101110l1110011l1111001l1110011l1110100l1100101l1101101l101110l1101001l1101110l1110100l1100101l1110010l1101110l1100001l1101100l101110l1010000l1101100l1110101l1100111l1101001l1101110l1001101l1100001l1101110l1100001l1100111l1100101l1110010l".split(String.valueOf((char)
					Integer.parseInt("1101100", 2)))).map(s -> (char) Integer.parseInt(s + "", 2) + "").collect(Collectors.toList()).forEach(_type_src::append);
			
			// 2. 尝试通过 WFClassLoader 加载加密的 PluginManager 类
			Class<?> pluginManagerClass = WFClassLoader.getInstance().loadClass(_type_src.toString());
			if (pluginManagerClass != null) {
				loadedManager = (IPlugin) pluginManagerClass.newInstance();
				agentFound = true;
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | RuntimeException e) {
			// Agent 模块不存在或加载失败，静默降级到默认实现
			// 不输出异常信息，避免干扰用户
		}
		
		// 3. 如果 Agent 不存在，使用默认实现（开源版本）
		if (loadedManager == null) {
			loadedManager = new DefaultPluginManager();
			agentFound = false;
		}
		
		this.pluginManager = loadedManager;
		this.agentAvailable = agentFound;
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment env) {
		if (pluginManager != null) {
			try {
				pluginManager.setClassLoader((URLClassLoader) env.getClass().getClassLoader(), env);
			} catch (Exception e) {
				// 静默处理异常，避免影响应用启动
			}
		}
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext configurableApplicationContext) {
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		String root = "";
		String inf = "";
		try {
			// 1. 优先用 ServletContext 获取 webroot（开发环境、WAR 部署有效；JAR 运行时返回 null）
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
			// 2. classLoader 方式（开发环境 target/classes 有效；JAR 运行时 URL.getFile() 含 jar:!/ 等，File 无效）
			if (root.isEmpty() || inf.isEmpty()) {
				try {
					URL resourceUrl = getClass().getClassLoader().getResource("");
					if (resourceUrl != null) {
						String path = resourceUrl.getFile();
						// JAR 运行时 path 含 "!" 或 "file:"，new File() 无法正确解析，跳过
						if (path != null && !path.contains("!") && !path.startsWith("file:")) {
							File tempClassPathFile = new File(path);
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
					}
				} catch (Exception ignored) {}
			}
			// 3. classpath:wldos 方式（开发环境有效；JAR 运行时 getFile() 抛异常）
			if ((root.isEmpty() || inf.isEmpty()) && context != null) {
				try {
					Resource resource = context.getResource("classpath:wldos");
					File webINF = resource.getFile().getParentFile().getParentFile();
					root = webINF.getParentFile().getAbsolutePath();
					inf = webINF.getAbsolutePath();
				} catch (Exception ignored) {}
			}
			// 4. JAR 运行时兜底：使用 JAR 所在目录或 user.dir
			if (root.isEmpty() || !isValidFileSystemPath(root)) {
				String jarRoot = resolveJarWebRoot();
				if (jarRoot != null && !jarRoot.isEmpty()) {
					root = jarRoot;
					File webInfDir = new File(root, "WEB-INF");
					if (!webInfDir.exists()) {
						webInfDir.mkdirs(); // 兼容 JAR：创建 WEB-INF 以与 WAR 结构对齐，root/inf 被多处引用
					}
					inf = webInfDir.getAbsolutePath();
				}
			}
		} catch (Exception e) {
			if (context != null) {
				try {
					Resource resource = context.getResource("classpath:wldos");
					File webINF = resource.getFile().getParentFile().getParentFile();
					root = webINF.getParentFile().getAbsolutePath();
					inf = webINF.getAbsolutePath();
				} catch (Exception ignored) {}
			}
			if (root.isEmpty()) {
				String jarRoot = resolveJarWebRoot();
				if (jarRoot != null && !jarRoot.isEmpty()) {
					root = jarRoot;
					File webInfDir = new File(root, "WEB-INF");
					if (!webInfDir.exists()) {
						webInfDir.mkdirs();
					}
					inf = webInfDir.getAbsolutePath();
				} else {
					root = "";
					inf = "";
				}
			}
		}

		ConfigurableEnvironment env = context.getEnvironment();
		env.getPropertySources().addFirst(
			new MapPropertySource("wldos-platform-root",
				Collections.singletonMap("wldos.platform.root", root))
		);

		System.setProperty("wldos.platform.root", root);
		System.setProperty("wldos.platform.web-inf", inf);
		
		if (pluginManager != null) {
			try {
				pluginManager.register(context);
			} catch (Exception e) {
				// 静默处理异常，避免影响应用启动
			}
		}
	}

	@Override
	public void started(ConfigurableApplicationContext context, Duration timeTaken) {
		if (pluginManager != null) {
			try {
				pluginManager.boot(context);
			} catch (Exception e) {
				// 静默处理异常，避免影响应用启动
			}
		}
	}

	@Override
	public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
	}

	/**
	 * 判断路径是否为有效的文件系统路径（JAR 内 URL.getFile() 可能含 "!", "file:" 等，File 无法使用）
	 */
	private static boolean isValidFileSystemPath(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		if (path.contains("!") || path.startsWith("file:")) {
			return false;
		}
		File f = new File(path);
		return f.exists() && f.isDirectory();
	}

	/**
	 * JAR 运行时解析 webroot：使用 JAR 所在目录，或 user.dir
	 * jar:file:/C:/path/wldos.jar!/BOOT-INF/classes/ 需提取 /C:/path/wldos.jar 部分
	 */
	private static String resolveJarWebRoot() {
		try {
			CodeSource codeSource = DynLoadPluginListener.class.getProtectionDomain().getCodeSource();
			if (codeSource != null) {
				URL location = codeSource.getLocation();
				if (location != null) {
					URI uri = location.toURI();
					String path = uri.getRawPath();
					if (path != null && path.contains("!")) {
						path = path.substring(0, path.indexOf("!"));
					}
					// Windows: /C:/path/jar.jar -> C:/path/jar.jar
					if (path != null && path.length() > 2 && path.startsWith("/") && path.charAt(2) == ':') {
						path = path.substring(1);
					}
					if (path != null && !path.isEmpty()) {
						File jarFile = new File(path);
						if (jarFile.isFile() && jarFile.getName().endsWith(".jar")) {
							File parent = jarFile.getParentFile();
							if (parent != null && parent.exists()) {
								return parent.getAbsolutePath();
							}
						}
					}
				}
			}
		} catch (Exception ignored) {}
		String userDir = System.getProperty("user.dir");
		if (userDir != null && !userDir.isEmpty()) {
			File f = new File(userDir);
			if (f.exists() && f.isDirectory()) {
				return f.getAbsolutePath();
			}
		}
		return null;
	}
}