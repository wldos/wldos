/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.framework.support.audit.config;

import io.github.wldos.framework.support.audit.ILoginLogger;
import io.github.wldos.framework.support.audit.IOpLogger;
import io.github.wldos.framework.support.audit.ISystemLogger;
import com.wldos.framework.support.audit.impl.DefaultLoginLogger;
import com.wldos.framework.support.audit.impl.DefaultOpLogger;
import com.wldos.framework.support.audit.impl.DefaultSystemLogger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 审计日志契约默认实现的装配类。
 *
 * <p>设计要点：{@code @ConditionalOnMissingBean} 必须放在 {@code @Configuration} 的
 * {@code @Bean} 方法上才能稳定生效；放在 {@code @Component} 类上时 Spring Boot 文档明确不推荐，
 * 评估顺序非确定可能导致默认实现未被注册（业务侧 {@code @Autowired(required=false)} 取到 null，
 * 日志静默丢失）。本类把三个默认 Logger 统一以 {@code @Bean} 方式装配，保证：
 * <ul>
 *   <li>无商业/插件覆盖时，{@link DefaultLoginLogger}/{@link DefaultOpLogger}/{@link DefaultSystemLogger}
 *       一定被激活；</li>
 *   <li>商业模块或第三方提供同类型 {@code @Bean} 时，自动跳过默认实现。</li>
 * </ul>
 *
 * @author 元悉宇宙
 * @date 2026/05/04
 * @version 1.0
 */
@Configuration
public class AuditAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ILoginLogger.class)
	public ILoginLogger defaultLoginLogger() {
		return new DefaultLoginLogger();
	}

	@Bean
	@ConditionalOnMissingBean(IOpLogger.class)
	public IOpLogger defaultOpLogger() {
		return new DefaultOpLogger();
	}

	@Bean
	@ConditionalOnMissingBean(ISystemLogger.class)
	public ISystemLogger defaultSystemLogger() {
		return new DefaultSystemLogger();
	}
}
