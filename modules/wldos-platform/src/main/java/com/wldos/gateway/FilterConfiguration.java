/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.gateway;

import java.util.Collections;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置。
 *
 * @author 元悉宇宙
 * @date 2021/9/2
 * @version 1.0
 */
@Configuration
public class FilterConfiguration {
	@Bean
	FilterRegistrationBean<EdgeGateWayFilter> edgeGateWayFilterRegistrationBean() {
		FilterRegistrationBean<EdgeGateWayFilter> filter = new FilterRegistrationBean<>();
		filter.setFilter(new EdgeGateWayFilter());
		filter.setOrder(-1);
		filter.setUrlPatterns(Collections.singletonList("/*"));

		return filter;
	}
}