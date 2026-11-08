/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.config;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.github.wldos.common.utils.ObjectUtils;
import com.wldos.platform.core.service.OptionsNoRepoService;
import io.github.wldos.platform.support.PropertiesDyn;
import io.github.wldos.platform.support.system.entity.WoOptions;
import io.github.wldos.platform.support.system.enums.OptionTypeEnum;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 加载配置信息。
 * 依赖 dataSourceInitializer 确保数据库初始化完成后再加载配置。
 *
 * @author Yuanxi Universe
 * @date 2021/7/13
 * @version 1.0
 */
@Slf4j
@Configuration
@DependsOn("dataSourceInitializer")
public class PropertiesReader {
	@SuppressWarnings("all")
	@Autowired
	@Lazy
	private PropertiesDyn propsDyn;

	private final ConfigurableEnvironment env;

	private final OptionsNoRepoService service;

	private final ContextRefresher contextRefresher;

	@SuppressWarnings("all")
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
			log.error("Error during database properties load {}", ObjectUtils.string(e.getMessage()));
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

			log.debug("Database properties reloaded: {}", propertyMap);
		}
		catch (Exception e) {
			log.error("Error during database properties load {}", ObjectUtils.string(e.getMessage()));
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
			log.error("Error during dyn properties set {}", ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	/**
	 * Mesh 等基础设施组件专用的轻量属性传播：不重算业务默认值兜底，只叠加给定 key 并刷新，
	 * 供高频后台周期性同步场景（如 Mesh 集群策略 gossip）使用，避免每次都牵动全量业务属性重算。
	 * 与 {@link #dynSetPropsSrc}（面向管理端一次性、低频的业务参数变更）分开，语义不混用。
	 */
	public void applyMeshPolicyProps(Map<String, String> propertyMap) {
		try {
			this.propsDyn.applyMeshPolicyProps(this.env, propertyMap, this.propertyName, this.contextRefresher);
		}
		catch (Exception e) {
			log.error("Error during mesh policy props apply {}", ObjectUtils.string(e.getMessage()));
			throw new RuntimeException(e);
		}
	}
}