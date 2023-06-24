/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.NoRepoController;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.conf.PropertiesReader;
import com.wldos.support.system.entity.WoOptions;
import com.wldos.sys.base.service.OptionsNoRepoService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class OptionsController extends NoRepoController<OptionsNoRepoService> {

	private final PropertiesReader propertiesReader;

	public OptionsController(PropertiesReader propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	@GetMapping("")
	public Map<String, String> fetchAutoReloadOptions() {
		List<WoOptions> allSysOptions = this.service.getSystemOptions();
		return allSysOptions.stream().collect(Collectors.toMap(WoOptions::getOptionKey, WoOptions::getOptionValue, (k1, k2) -> k1));
	}

	@PostMapping("config")
	public String configSysOptions(@RequestBody Map<String, Object> config) {
		Map<String, String> propertyMap = this.service.configSysOptions(config);

		this.propertiesReader.dynSetPropsSrc(propertyMap);
		return this.resJson.ok("ok");
	}

	/**
	 * 刷新所有自动加载配置
	 */
	@GetMapping("refresh")
	public String refresh() {
		this.propertiesReader.reLoadDBPropsSrc();

		return this.resJson.ok("ok");
	}
}