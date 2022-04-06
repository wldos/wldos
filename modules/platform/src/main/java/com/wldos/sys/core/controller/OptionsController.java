/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import com.wldos.base.controller.NoRepoController;
import com.wldos.conf.PropertiesReader;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置项controller。
 *
 * @author 树悉猿
 * @date 2021/7/14
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("system/options")
public class OptionsController extends NoRepoController {

	private final PropertiesReader propertiesReader;

	public OptionsController(PropertiesReader propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	/**
	 * 刷新配置接口
	 * 使用@ConfigurationProperties注解的配置类会刷新实例，使用@Value注入的参数值，需要配合@RefreshScope注解才能刷新对应的值。
	 *
	 */
	@GetMapping("refresh")
	public String refresh() {
		this.propertiesReader.reLoadDBPropsSrc();

		return this.resJson.ok("ok");
	}
}
