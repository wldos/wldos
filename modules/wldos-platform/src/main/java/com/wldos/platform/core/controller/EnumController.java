/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.platform.audit.enums.SysLogActorKindEnum;
import com.wldos.platform.core.enums.UserStatusEnum;

import io.github.wldos.common.vo.SelectOption;
import io.github.wldos.framework.support.plugins.core.AutoStartEnum;
import io.github.wldos.framework.support.plugins.core.PluginStatus;
import io.github.wldos.platform.support.rbac.DataScope;
import io.github.wldos.platform.support.rbac.ResourceScopeLevel;
import io.github.wldos.platform.support.resource.enums.MenuNodeKindEnum;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import io.github.wldos.platform.support.resource.enums.ResourceMenuRegionEnum;
import io.github.wldos.platform.support.resource.enums.ResourceTerminalEnum;
import io.github.wldos.platform.support.system.enums.OptionAppCodeEnum;
import io.github.wldos.platform.support.system.enums.OptionTypeEnum;
import io.github.wldos.platform.support.web.enums.TemplateTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 常用枚举controller。
 *
 * @author Yuanxi Universe
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

	@ApiOperation(value = "资源授权层级枚举", notes = "wo_resource.scope_level")
	@GetMapping("select/scopeLevel")
	public List<SelectOption> fetchEnumScopeLevel() {
		return Arrays.stream(ResourceScopeLevel.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	@ApiOperation(value = "资源可见终端枚举", notes = "wo_resource.terminals")
	@GetMapping("select/resourceTerminal")
	public List<SelectOption> fetchEnumResourceTerminal() {
		return Arrays.stream(ResourceTerminalEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	@ApiOperation(value = "菜单展示区域枚举", notes = "wo_resource.menu_region")
	@GetMapping("select/resourceMenuRegion")
	public List<SelectOption> fetchEnumResourceMenuRegion() {
		return Arrays.stream(ResourceMenuRegionEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	@ApiOperation(value = "菜单节点形态枚举", notes = "wo_resource.menu_node_kind")
	@GetMapping("select/menuNodeKind")
	public List<SelectOption> fetchEnumMenuNodeKind() {
		return Arrays.stream(MenuNodeKindEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	@ApiOperation(value = "角色数据策略枚举", notes = "wo_role.data_policy")
	@GetMapping("select/dataPolicy")
	public List<SelectOption> fetchEnumDataPolicy() {
		return Arrays.stream(DataScope.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	/**
	 * 全局系统选项：{@code wo_options.option_type}（与 {@link OptionTypeEnum} 一致）
	 */
	@ApiOperation(value = "系统选项类型", notes = "wo_options.option_type：auto_reload / normal")
	@GetMapping("select/optionType")
	public List<SelectOption> fetchEnumWoOptionType() {
		return Arrays.stream(OptionTypeEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	/**
	 * 全局系统选项：{@code wo_options.app_code}（与 {@link OptionAppCodeEnum} 一致）
	 */
	@ApiOperation(value = "系统选项应用编码", notes = "wo_options.app_code，空串表示未归属应用")
	@GetMapping("select/optionAppCode")
	public List<SelectOption> fetchEnumWoOptionAppCode() {
		return Arrays.stream(OptionAppCodeEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
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

	@ApiOperation(value = "用户状态枚举", notes = "wo_user.status")
	@GetMapping("select/userStatus")
	public List<SelectOption> fetchEnumUserStatus() {
		return Arrays.stream(UserStatusEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	@ApiOperation(value = "系统日志来源分类", notes = "wo_sys_log metadata.actorKind")
	@GetMapping("select/sysLogActorKind")
	public List<SelectOption> fetchEnumSysLogActorKind() {
		return Arrays.stream(SysLogActorKindEnum.values())
				.map(item -> SelectOption.of(item.getLabel(), item.getValue()))
				.collect(Collectors.toList());
	}
}
