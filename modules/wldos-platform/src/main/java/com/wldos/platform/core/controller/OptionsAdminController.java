/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.service.OptionsService;
import com.wldos.platform.support.system.entity.WoOptions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 系统配置相关controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "系统配置管理（后台）")
@RestController
@RequestMapping("admin/sys/options")
public class OptionsAdminController extends EntityController<OptionsService, WoOptions> {
	@ApiOperation(value = "所有系统配置", notes = "获取所有系统配置")
	@GetMapping("")
	public List<WoOptions> fetchAllOptions() {
		return this.service.findAll();
	}

}
