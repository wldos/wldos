/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.conf;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.wldos.system.core.entity.WoOptions;
import com.wldos.system.core.service.OptionsService;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Configuration
public class PropertiesReader {

	private final ConfigurableEnvironment env;

	private final OptionsService service;

	public PropertiesReader(ConfigurableEnvironment env, OptionsService service) {
		this.env = env;
		this.service = service;
	}

	private final String propertyName = "dbPropsSrc";

	@PostConstruct
	public void initialDBPropsSrc() {
		MutablePropertySources propertySources = env.getPropertySources();
		try {
			Map<String, String> propertyMap = this.service.getAllByAppType().stream().collect(Collectors.toMap(WoOptions::getKey, WoOptions::getValue, (k1, k2) -> k1));

			Properties properties = new Properties();
			properties.putAll(propertyMap);

			PropertiesPropertySource dbPropertySource = new PropertiesPropertySource(this.propertyName, properties);

			Pattern defaultConfReg = Pattern.compile("^applicationConfig.*"); // 匹配springboot系统默认配置的正则前缀

			String name = null;
			boolean flag = false;

			for (PropertySource<?> source : propertySources) {
				if (defaultConfReg.matcher(source.getName()).matches()) {
					name = source.getName();
					flag = true;
					log.info("Find propertySources ".concat(name));
					break;
				}
			}

			log.info("*****************************读取配置开始********************************");

			if (flag) { // 以application.properties为准，以数据库配置为准,则addBefore...
				propertySources.addAfter(name, dbPropertySource);
			}
			else { // 没有文件配置，则置前当前配置
				propertySources.addFirst(dbPropertySource);
			}
			log.info("*****************************读取配置结束********************************");
		}
		catch (Exception e) {
			log.error("Error during database properties load", e);
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
		}
		catch (Exception e) {
			log.error("Error during database properties reload", e);
			throw new RuntimeException(e);
		}
	}
}