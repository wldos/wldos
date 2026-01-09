/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.common.vo.SelectOption;
import com.wldos.framework.support.plugins.core.AutoStartEnum;
import com.wldos.framework.support.plugins.core.PluginStatus;
import com.wldos.platform.support.resource.enums.ResourceEnum;
import com.wldos.platform.support.web.enums.TemplateTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 常用枚举controller。
 *
 * @author 元悉宇宙
 * @date 2022/3/7
 * @version 1.0
 */
@Api(tags = "常用枚举")
@RequestMapping("enum")
@RestController
public class EnumController {
	/**
	 * 模板类型枚举
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "模板类型枚举", notes = "获取模板类型枚举列表")
	@GetMapping("select/template")
	public List<SelectOption> fetchEnumTempType() {

		return Arrays.stream(TemplateTypeEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	/**
	 * 资源类型枚举
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "资源类型枚举", notes = "获取资源类型枚举列表")
	@GetMapping("select/resource")
	public List<SelectOption> fetchEnumResType() {
		return Arrays.stream(ResourceEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	/**
	 * 插件状态枚举
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "插件状态枚举", notes = "获取插件状态枚举列表")
	@GetMapping("select/pluginStatus")
	public List<SelectOption> fetchEnumPluginStatus() {
		return Arrays.stream(PluginStatus.values()).map(item -> SelectOption.of(item.getDescription(), item.name())).collect(Collectors.toList());
	}

	/**
	 * AutoStartEnum枚举
	 *
	 * @return 枚举列表
	 */
	@ApiOperation(value = "自动启动枚举", notes = "获取AutoStartEnum枚举列表")
	@GetMapping("select/autoStart")
	public List<SelectOption> fetchEnumAutoStart() {
		return Arrays.stream(AutoStartEnum.values()).map(item -> SelectOption.of(item.getDescription(), item.getEnumName())).collect(Collectors.toList());
	}
}
