/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.calendar.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 节假日日历配置类：装配契约的默认实现 Bean。
 *
 * <p>按 wldos 契约模式（见 wldos-framework 的 WldosFrameworkAutoConfiguration 范式）：
 * 在 {@code @Configuration} 中用 {@code @Bean + @ConditionalOnMissingBean} 装配 default 实现；
 * 站点提供同接口实现的 {@code @Component / @Bean} 时，default 自动让位。
 *
 * <p>不要把 {@code @ConditionalOnMissingBean} 直接写在 {@link DefaultHolidayExternalSource} 的
 * {@code @Component} 上——Spring Boot 评估时会把"待注册的 default 自身"也算入 typecheck，
 * 触发"已存在 bean"误判，反而导致默认实现被排除、容器里没有任何 HolidayExternalSource。
 *
 * @author 元悉宇宙
 * @date 2026/04/30
 * @version 1.0
 */
@Configuration
public class HolidayCalendarConfiguration {

	/**
	 * 当容器中没有任何 {@link HolidayExternalSource} 实现时，注册 noop 默认实现，
	 * 保证 {@link HolidaySyncService} 总能注入到 bean、流程不中断。
	 */
	@Bean
	@ConditionalOnMissingBean(HolidayExternalSource.class)
	public HolidayExternalSource defaultHolidayExternalSource() {
		return new DefaultHolidayExternalSource();
	}
}
