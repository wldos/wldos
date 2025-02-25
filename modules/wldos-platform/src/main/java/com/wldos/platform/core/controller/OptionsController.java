/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.platform.config.PropertiesReader;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.service.OptionsNoRepoService;
import com.wldos.platform.support.system.entity.WoOptions;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置项controller。
 *
 * @author 元悉宇宙
 * @date 2021/7/14
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("system/options")
public class OptionsController extends NonEntityController<OptionsNoRepoService> {

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