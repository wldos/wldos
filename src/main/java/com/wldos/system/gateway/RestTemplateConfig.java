/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.gateway;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * rest模板相关配置。
 *
 * @Title RestTemplateConfig
 * @Package com.wldos.system.gateway
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/8
 * @Version 1.0
 */
@Configuration
public class RestTemplateConfig {

	@Value("${restemplate.connection.timeout}")
	private int restConnTimeout;

	@Value("${restemplate.read.timeout}")
	private int restReadTimeout;

	@Bean
	//@LoadBalanced
	public RestTemplate restTemplate(ClientHttpRequestFactory simleClientHttpRequestFactory) {// 此配置暂时没有用！
		RestTemplate restTemplate = new RestTemplate();
		//配置自定义的message转换器
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		MappingJackson2HttpMessageConverter jsonConverter = new CustomMappingJackson2HttpMessageConverter();
		messageConverters.add(jsonConverter);
		restTemplate.setMessageConverters(messageConverters);

		return restTemplate;
	}


	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory reqFactory = new SimpleClientHttpRequestFactory();
		reqFactory.setConnectTimeout(this.restConnTimeout);
		reqFactory.setReadTimeout(this.restReadTimeout);
		return reqFactory;
	}
}
