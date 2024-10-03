/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support;

import java.util.Map;

import com.wldos.support.system.OptionsOpener;

import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author 元悉宇宙
 * @date 2022/1/28
 * @version 1.0
 */
public interface PropertiesDyn {
	/**
	 * 合并数据库配置属性到运行时配置环境
	 *
	 * @param env 配置环境
	 * @param propertyName 数据库配置别名
	 * @param contextRefresher 会话刷新器
	 */
	void initialDBProps(ConfigurableEnvironment env, String propertyName, ContextRefresher contextRefresher, OptionsOpener service);

	/**
	 * 重新加载数据库属性配置
	 *
	 * @param env 配置环境
	 * @param propertyMap 属性集
	 * @param propertyName 数据库配置别名
	 * @param contextRefresher 会话刷新器
	 */
	void reLoadDBPropsSrc(ConfigurableEnvironment env, Map<String, String> propertyMap, String propertyName, ContextRefresher contextRefresher);
}
