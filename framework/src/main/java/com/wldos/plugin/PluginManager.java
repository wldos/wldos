/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.wldos.common.Constants;
import com.wldos.support.storage.utils.StoreUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * 实现运行时安装插件和产品插件化，打造插件化开发生态。
 * 支持热插拔。
 */
@SuppressWarnings({ "unchecked" })
public class PluginManager {

	private URLClassLoader classLoader;

	private static final List<Class<?>> springIOCAnnotations = new ArrayList<>();

	private static final List<PluginBootStrap> bootClasses = new ArrayList<>();

	//可以放进spring容器的类的注解，待优化
	static {
		springIOCAnnotations.add(Controller.class);
		springIOCAnnotations.add(Service.class);
		springIOCAnnotations.add(Repository.class);
		springIOCAnnotations.add(Component.class);
	}

	public void setClassLoader(URLClassLoader urlClassLoader) {
		this.classLoader = urlClassLoader;
	}

	public void register(ConfigurableApplicationContext context) {
		List<Plugin> plugins = getPlugins();
		plugins.forEach(plugin -> {
			addJarToClasspath(plugin);

			JarFile jarFile = readJarFile(plugin);
			Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

			fetchBootClass(plugin);

			if (plugin.getScanPath() != null) {
				//遍历jar包，将指定路径下的类添加到spring容器
				traverseJar(jarEntryEnumeration, context, plugin);
			}
		});
	}

	public void boot(ConfigurableApplicationContext context) {
		bootClasses.forEach(bootClass -> bootClass.boot(context));
	}

	// 结合数据库实现可安装、可卸载的插件管理（已安装的打标记）
	@SneakyThrows
	private List<Plugin> getPlugins() {
		List<Plugin> pluginList = new ArrayList<>();

		InputStream jarStream = null;
		try {
			ClassPathResource loader = new ClassPathResource("/include/wldos-agent-release.jar");
			jarStream = loader.getInputStream();

			String resPath = System.getProperty("wldos.platform.web-inf") + File.separator + Constants.DIRECTORY_TEMP_NAME + File.separator+"include"+File.separator+"wldos-agent-release.jar";

			StoreUtils.saveAsFile(jarStream, resPath);

			jarStream.close();

			File jar = new File(resPath);
			if (!jar.exists())
				throw new RuntimeException("加载框架扩展异常: no found extend ");

			Plugin plugin = new Plugin();
			plugin.setName(jar.getName());
			plugin.setUrl(resPath);
			plugin.setScanPath("com.wldos"); // 默认jar放在本平台包下
			pluginList.add(plugin);
		}
		catch (Exception e) {
			throw new RuntimeException("加载框架扩展异常", e);
		} finally {
			if (jarStream != null) {
				jarStream.close();
			}
		}

		return pluginList;
	}

	private void fetchBootClass(Plugin plugin) {
		if (plugin.getBootClass() == null || plugin.getBootClass().trim().isEmpty()) {
			return;
		}

		try {
			PluginBootStrap pluginBootStrap = (PluginBootStrap) classLoader.loadClass(plugin.getBootClass()).newInstance();
			bootClasses.add(pluginBootStrap);
		}
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("获取插件boot函数失败，插件名称：" + plugin.getName());
		}
	}

	private void traverseJar(Enumeration<JarEntry> jarEntryEnumeration, ConfigurableApplicationContext context,
			Plugin plugin) {
		while (jarEntryEnumeration.hasMoreElements()) {
			JarEntry jarEntry = jarEntryEnumeration.nextElement();
			if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")
					|| plugin.getScanPath().stream().noneMatch(scanPath -> jarEntry.getName().startsWith(scanPath))) {
				continue;
			}

			String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
			className = className.replace('/', '.');

			try {
				Class<?> c = classLoader.loadClass(className);
				BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) context.getBeanFactory();
				for (Class<?> springIOCAnnotationClass : springIOCAnnotations) {
					if (c.getAnnotation((Class<? extends Annotation>) springIOCAnnotationClass) != null) {
						definitionRegistry.registerBeanDefinition(className,
								BeanDefinitionBuilder.genericBeanDefinition(c).getBeanDefinition());
						break;
					}
				}
			}
			catch (NoClassDefFoundError | ClassNotFoundException e1) {
				traverseJar(jarEntryEnumeration, context, plugin);
			}
		}
	}

	private void addJarToClasspath(Plugin plugin) {
		try {
			File file = new File(plugin.getUrl());
			URL url = file.toURI().toURL();

			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(classLoader, url);
		}
		catch (IllegalAccessException | MalformedURLException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("添加插件jar包到classpath失败，插件名称：" + plugin.getName());
		}
	}

	private JarFile readJarFile(Plugin plugin) {
		try {
			return new JarFile(plugin.getUrl());
		}
		catch (IOException e) {
			throw new RuntimeException("获取插件中的文件信息失败，插件名称：" + plugin.getName(), e);
		}
	}
}