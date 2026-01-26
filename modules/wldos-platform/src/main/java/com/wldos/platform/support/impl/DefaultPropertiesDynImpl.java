/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.impl;

import com.wldos.platform.support.PropertiesDyn;
import com.wldos.platform.support.system.OptionsOpener;
import com.wldos.platform.support.system.entity.WoOptions;
import com.wldos.platform.support.system.enums.OptionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 默认 PropertiesDyn 实现类（开源版本）
 * 当 Agent 模块中的 PropertiesDynImpl 不存在时使用此实现
 * 使用降级处理：简化实现，直接从数据库读取配置并合并到环境变量中
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@Slf4j
@ConditionalOnMissingClass("com.wldos.support.conf.PropertiesDynImpl")
@Component("propsDyn")
public class DefaultPropertiesDynImpl implements PropertiesDyn {

	@Override
	public void initialDBProps(ConfigurableEnvironment env, String propertyName, ContextRefresher contextRefresher, OptionsOpener service) {
		try {
			// 降级处理：简化实现，直接从数据库读取系统配置（仅自动重载类型）
			Map<String, String> propertyMap = service.getAllSysOptionsByOptionType(OptionTypeEnum.AUTO_RELOAD.getValue())
					.stream()
					.collect(Collectors.toMap(
							WoOptions::getOptionKey,
							option -> option.getOptionValue() != null ? option.getOptionValue() : "",
							(k1, k2) -> k1));

			if (!propertyMap.isEmpty()) {
				Properties properties = new Properties();
				properties.putAll(propertyMap);

				MutablePropertySources propertySources = env.getPropertySources();
				propertySources.addFirst(new PropertiesPropertySource(propertyName, properties));

				log.info("已加载 {} 条数据库配置属性", propertyMap.size());
			}
		} catch (Exception e) {
			log.error("加载数据库配置属性失败: {}", e.getMessage(), e);
			// 降级处理：不抛出异常，允许应用继续启动
		}
	}

	@Override
	public void reLoadDBPropsSrc(ConfigurableEnvironment env, Map<String, String> propertyMap, String propertyName, ContextRefresher contextRefresher) {
		try {
			// 降级处理：简化实现，直接更新属性源
			MutablePropertySources propertySources = env.getPropertySources();
			
			// 移除旧的属性源
			if (propertySources.contains(propertyName)) {
				propertySources.remove(propertyName);
			}

			// 添加新的属性源
			if (propertyMap != null && !propertyMap.isEmpty()) {
				Properties properties = new Properties();
				properties.putAll(propertyMap);
				propertySources.addFirst(new PropertiesPropertySource(propertyName, properties));

				log.info("已重新加载 {} 条数据库配置属性", propertyMap.size());
			}

			// 刷新上下文（如果支持）
			if (contextRefresher != null) {
				contextRefresher.refresh();
			}
		} catch (Exception e) {
			log.error("重新加载数据库配置属性失败: {}", e.getMessage(), e);
			// 降级处理：不抛出异常，允许应用继续运行
		}
	}
}
