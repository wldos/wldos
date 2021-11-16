/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.system.gateway;

import java.util.Collections;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置。
 *
 * @author 树悉猿
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
