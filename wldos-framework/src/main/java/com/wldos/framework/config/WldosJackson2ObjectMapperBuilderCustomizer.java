/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License, Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使 {@code spring.jackson.date-format}、{@code spring.jackson.time-zone}、{@code non_null} 等在
 * 所有注入 {@link com.fasterxml.jackson.databind.ObjectMapper} 的路径上生效（含 MVC、ResultJson、
 * CustomMessageConverter 等），并与原有「Long/BigInteger 序列化为字符串」行为一致。
 * <p>
 * 字段上的 {@link com.fasterxml.jackson.annotation.JsonFormat} 仍优先生效，不受此处影响。
 *
 * @author Yuanxi Universe
 * @date 2026-05-12
 */
@Configuration
public class WldosJackson2ObjectMapperBuilderCustomizer {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer wldosJacksonDefaults() {
		return builder -> {
			/* 与spring.jackson.date-format 配合：java.util.Date 等输出字符串而非时间戳 */
			builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			/* 勿使用 builder.modules(...) 覆盖预置 Module（会丢掉 JavaTimeModule） */
			builder.serializerByType(Long.class, ToStringSerializer.instance);
			builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
			builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
		};
	}
}
