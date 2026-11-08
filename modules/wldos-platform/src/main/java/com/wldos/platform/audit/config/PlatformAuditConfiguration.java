/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.audit.config;

import com.wldos.framework.support.audit.impl.DefaultSystemLogger;
import com.wldos.platform.audit.support.PlatformSystemLogger;
import com.wldos.platform.audit.support.SystemLogContextEnricher;
import io.github.wldos.framework.support.audit.ISystemLogger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 平台审计扩展：系统日志入队前补 IP 与租户/站点。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Configuration
public class PlatformAuditConfiguration {

	@Bean
	@ConditionalOnMissingBean(DefaultSystemLogger.class)
	DefaultSystemLogger platformDefaultSystemLogger() {
		return new DefaultSystemLogger();
	}

	@Bean
	@Primary
	public ISystemLogger systemLogger(DefaultSystemLogger platformDefaultSystemLogger,
			SystemLogContextEnricher contextEnricher) {
		return new PlatformSystemLogger(platformDefaultSystemLogger, contextEnricher);
	}
}
