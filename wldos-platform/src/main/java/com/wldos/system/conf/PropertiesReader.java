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
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.wldos.system.core.entity.WoOptions;
import com.wldos.system.core.service.OptionsNoRepoService;
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

	private final OptionsNoRepoService service;

	public PropertiesReader(ConfigurableEnvironment env, OptionsNoRepoService service) {
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

			String configurationProperties = "configurationProperties";

			String name = null;
			boolean flag = false;

			for (PropertySource<?> source : propertySources) {
				if (configurationProperties.equals(source.getName())) {
					name = source.getName();
					flag = true;
					log.info("Find propertySources ".concat(name));
					break;
				}
			}

			log.info("*****************************读取配置开始********************************");

			if (flag) {
				propertySources.addAfter(name, dbPropertySource);
				log.info("已加载系统配置源：".concat(this.propertyName));
			}
			else {
				propertySources.addFirst(dbPropertySource);
			}
			log.info("*****************************读取配置结束********************************");
		}
		catch (Exception e) {
			log.error("Error during database properties load", e);
			throw new RuntimeException(e);
		}
	}

	public void reLoadDBPropsSrc() {
		MutablePropertySources propertySources = env.getPropertySources();
		try {
			Map<String, String> propertyMap = this.service.getAllByAppType().stream().collect(Collectors.toMap(WoOptions::getKey, WoOptions::getValue, (k1, k2) -> k1));

			Properties properties = new Properties();
			properties.putAll(propertyMap);

			PropertiesPropertySource dbPropertySource = new PropertiesPropertySource(this.propertyName, properties);

			propertySources.addFirst(dbPropertySource);
		}
		catch (Exception e) {
			log.error("Error during database properties reload", e);
			throw new RuntimeException(e);
		}
	}
}