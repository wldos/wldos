/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.conf;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.PropertiesDyn;
import com.wldos.sys.base.entity.WoOptions;
import com.wldos.sys.base.service.OptionsNoRepoService;

import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * 加载配置信息。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @version 1.0
 */
@Configuration
public class PropertiesReader implements PropertiesDyn {

	private final ConfigurableEnvironment env;

	private final OptionsNoRepoService service;

	private final ContextRefresher contextRefresher;

	public PropertiesReader(ConfigurableEnvironment env, OptionsNoRepoService service, ContextRefresher contextRefresher) {
		this.env = env;
		this.service = service;
		this.contextRefresher = contextRefresher;
	}

	private final String propertyName = "dbPropsSrc";

	@PostConstruct
	private void initialDBPropsSrc() {
		MutablePropertySources propertySources = env.getPropertySources();
		try {
			Map<String, String> propertyMap = this.service.getAllByAppType().stream().collect(Collectors.toMap(WoOptions::getKey, WoOptions::getValue, (k1, k2) -> k1));

			Properties properties = new Properties();
			properties.putAll(propertyMap);

			PropertiesPropertySource dbPropertySource = new PropertiesPropertySource(this.propertyName, properties);

			String configurationProperties = "configurationProperties"; // 匹配springboot系统默认配置的正则前缀

			String name = null;
			boolean flag = false;

			for (PropertySource<?> source : propertySources) {
				if (configurationProperties.equals(source.getName())) {
					name = source.getName();
					flag = true;
					System.out.println("Find propertySources ".concat(name));
					break;
				}
			}

			System.out.println("*****************************读取配置开始********************************");

			if (flag) { // 以application.properties为准，以数据库配置为准,则addBefore...
				propertySources.addAfter(name, dbPropertySource);
				System.out.println("已加载系统配置源：".concat(this.propertyName));
			}
			else { // 没有文件配置，则置前当前配置
				propertySources.addFirst(dbPropertySource);
			}
			//异步调用refresh方法，避免阻塞一直等待无响应
			new Thread(contextRefresher::refresh).start();
			System.out.println("已刷新会话环境所有参量");
			System.out.println("*****************************读取配置结束********************************");
		}
		catch (Exception e) {
			System.out.println("Error during database properties load" + ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	/**
	 * 重新装载系统配置
	 */
	public void reLoadDBPropsSrc() {
		MutablePropertySources propertySources = env.getPropertySources();
		try {
			Map<String, String> propertyMap = this.service.getAllByAppType().stream().collect(Collectors.toMap(WoOptions::getKey, WoOptions::getValue, (k1, k2) -> k1));

			Properties properties = new Properties();
			properties.putAll(propertyMap);

			PropertiesPropertySource dbPropertySource = new PropertiesPropertySource(this.propertyName, properties);

			propertySources.addFirst(dbPropertySource); // 在上面初始化方法的基础上，只需要加载数据库配置
			System.out.println("成功新增会话参数");
			//异步调用refresh方法，避免阻塞一直等待无响应
			new Thread(contextRefresher::refresh).start();
			System.out.println("已刷新会话环境所有参量");
		}
		catch (Exception e) {
			System.out.println("Error during database properties load" + ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	/**
	 * 运行时修改系统配置
	 */
	public void dynSetPropsSrc(Map<String, String> propertyMap) {
		MutablePropertySources propertySources = env.getPropertySources();
		try {
			Properties properties = new Properties();
			properties.putAll(propertyMap);

			PropertiesPropertySource dbPropertySource = new PropertiesPropertySource(this.propertyName, properties);

			propertySources.addFirst(dbPropertySource); // 在上面初始化方法的基础上，只需要加载数据库配置

			//异步调用refresh方法，避免阻塞一直等待无响应
			new Thread(contextRefresher::refresh).start();
			System.out.println("设置运行时参数，已刷新会话环境所有参量");
		}
		catch (Exception e) {
			System.out.println("Error during dyn properties set" + ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	@Override
	public ConfigurableEnvironment getEnvironment() {
		return this.env;
	}
}