/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 配置转换器。
 *
 * @author 元悉宇宙
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