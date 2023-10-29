/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.conf;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.PropertiesDyn;
import com.wldos.support.system.entity.WoOptions;
import com.wldos.support.system.enums.OptionTypeEnum;
import com.wldos.sys.base.service.OptionsNoRepoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 加载配置信息。
 *
 * @author 树悉猿
 * @date 2021/7/13
 * @version 1.0
 */
@Configuration
public class PropertiesReader {
	@SuppressWarnings("all")
	@Autowired
	@Lazy
	private PropertiesDyn propsDyn;

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
		try {
			this.propsDyn.initialDBProps(this.env, this.propertyName, this.contextRefresher, this.service);
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
		try {
			Map<String, String> propertyMap = this.service.getAllSysOptionsByOptionType(OptionTypeEnum.AUTO_RELOAD.getValue())
					.stream().collect(Collectors.toMap(WoOptions::getOptionKey, WoOptions::getOptionValue, (k1, k2) -> k1));

			this.propsDyn.reLoadDBPropsSrc(this.env, propertyMap, this.propertyName, this.contextRefresher);
		}
		catch (Exception e) {
			System.out.println("Error during database properties load" + ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	/**
	 * 运行时修改某些系统配置
	 */
	public void dynSetPropsSrc(Map<String, String> propertyMap) {

		try {
			this.propsDyn.reLoadDBPropsSrc(this.env, propertyMap, this.propertyName, this.contextRefresher);
		}
		catch (Exception e) {
			System.out.println("Error during dyn properties set" + ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}
}