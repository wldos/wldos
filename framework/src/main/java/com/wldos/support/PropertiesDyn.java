/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support;

import java.util.Map;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 动态配置系统参数。
 *
 * @author 树悉猿
 * @date 2022/1/28
 * @version 1.0
 */
public interface PropertiesDyn {

	void dynSetPropsSrc(Map<String, String> propertyMap);

	ConfigurableEnvironment getEnvironment();
}
