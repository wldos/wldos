/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.Map;

import com.wldos.base.controller.RepoController;
import com.wldos.sys.base.entity.WoOptions;
import com.wldos.sys.base.service.OptionsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/options")
public class OptionsAdminController extends RepoController<OptionsService, WoOptions> {
	@GetMapping("")
	public String readOptions() {
		Map<String, String> options = System.getenv();
		return this.resJson.ok(options);
	}
}
