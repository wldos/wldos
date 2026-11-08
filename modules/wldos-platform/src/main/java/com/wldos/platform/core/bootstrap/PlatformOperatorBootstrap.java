/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.bootstrap;

import com.wldos.platform.core.service.PlatformOperatorBootstrapService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 启动时幂等确保 platform-operator 数据面与 options 指针（M1）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
@Order(25)
public class PlatformOperatorBootstrap implements ApplicationRunner {

	private final PlatformOperatorBootstrapService bootstrapService;

	@Value("${wldos.platform.operator.bootstrap.enabled:true}")
	private boolean enabled;

	public PlatformOperatorBootstrap(PlatformOperatorBootstrapService bootstrapService) {
		this.bootstrapService = bootstrapService;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			return;
		}
		try {
			this.bootstrapService.ensurePlatformOperatorReady();
		}
		catch (Exception ex) {
			log.error("[platform.operator] bootstrap 失败: {}", ex.getMessage(), ex);
		}
	}
}
