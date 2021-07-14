/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.config;

import com.wldos.support.controller.NoRepoController;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置项controller。
 *
 * @Title OptionsController
 * @Package com.wldos.support.config
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/7/14
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("system/options")
public class OptionsController extends NoRepoController {
	private final ContextRefresher contextRefresher;

	private final PropertiesReader propertiesReader;

	public OptionsController(ContextRefresher contextRefresher, PropertiesReader propertiesReader) {
		this.contextRefresher = contextRefresher;
		this.propertiesReader = propertiesReader;
	}

	/**
	 * 刷新配置接口
	 */
	@GetMapping("refresh")
	public String refresh() {
		this.propertiesReader.reLoadDBPropsSrc();
		contextRefresher.refresh();
		return this.resJson.ok("ok");
	}
}
