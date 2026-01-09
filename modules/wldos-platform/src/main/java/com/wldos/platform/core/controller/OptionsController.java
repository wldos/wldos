/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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
import com.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.service.OptionsNoRepoService;
import com.wldos.platform.support.system.entity.WoOptions;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 系统配置项controller。
 *
 * @author 元悉宇宙
 * @date 2021/7/14
 * @version 1.0
 */
@Api(tags = "系统配置项")
@Slf4j
@RestController
@RequestMapping("system/options")
public class OptionsController extends NonEntityController<OptionsNoRepoService> {

	private final PropertiesReader propertiesReader;

	public OptionsController(PropertiesReader propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	@ApiOperation(value = "自动加载配置", notes = "获取所有自动加载的系统配置，返回配置项的键值对")
	@ApiResponses({
		@ApiResponse(code = 200, message = "成功", response = Map.class, responseContainer = "Map")
	})
	@GetMapping("")
	public Map<String, String> fetchAutoReloadOptions() {
		List<WoOptions> allSysOptions = this.service.getSystemOptions();
		return allSysOptions.stream().collect(Collectors.toMap(WoOptions::getOptionKey, WoOptions::getOptionValue, (k1, k2) -> k1));
	}

	@ApiOperation(value = "配置系统选项", notes = "配置系统选项，参数为键值对形式，如：{\"key1\":\"value1\",\"key2\":\"value2\"}")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "配置项键值对", value = "系统配置项，格式为键值对，键为配置项key，值为配置项value", dataTypeClass = Map.class, paramType = "body", required = true, example = "{\"wldos_system_name\":\"WLDOS系统\",\"wldos_system_version\":\"2.0.0\"}")
	})
	@ApiResponses({
		@ApiResponse(code = 200, message = "配置成功", response = Result.class)
	})
	@PostMapping("config")
	public Result configSysOptions(@RequestBody Map<String, Object> config) {
		Map<String, String> propertyMap = this.service.configSysOptions(config);

		this.propertiesReader.dynSetPropsSrc(propertyMap);
		return Result.ok("ok");
	}

	/**
	 * 刷新所有自动加载配置
	 */
	@ApiOperation(value = "刷新配置", notes = "刷新所有自动加载配置")
	@ApiResponses({
		@ApiResponse(code = 200, message = "刷新成功", response = Result.class)
	})
	@GetMapping("refresh")
	public Result refresh() {
		this.propertiesReader.reLoadDBPropsSrc();

		return Result.ok("ok");
	}
}