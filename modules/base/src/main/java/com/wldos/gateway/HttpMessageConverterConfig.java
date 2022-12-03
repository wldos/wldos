/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 配置转换器。
 *
 * @author 树悉猿
 * @date 2021/9/17
 * @version 1.0
 */
@Configuration
public class HttpMessageConverterConfig {

	@Bean
	@ConditionalOnMissingBean
	public MappingJackson2HttpMessageConverter getMappingJJackson2HttpMessageConverter() {
		return new CustomMessageConverter();
	}
}
