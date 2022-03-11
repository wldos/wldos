/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.base.controller.NoRepoController;
import com.wldos.common.vo.SelectOption;
import com.wldos.sys.base.enums.TempTypeEnum;
import com.wldos.sys.base.enums.ResourceEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 枚举常用操作controller。
 *
 * @author 树悉猿
 * @date 2022/3/7
 * @version 1.0
 */
@RequestMapping("enum")
@RestController
public class EnumController extends NoRepoController {
	/**
	 * 模板类型枚举
	 *
	 * @return 枚举列表
	 */
	@GetMapping("select/template")
	public List<SelectOption> fetchEnumTempType() {

		return Arrays.stream(TempTypeEnum.values()).map(item -> new SelectOption(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	/**
	 * 资源类型枚举
	 *
	 * @return 枚举列表
	 */
	@GetMapping("select/resource")
	public List<SelectOption> fetchEnumResType() {

		return Arrays.stream(ResourceEnum.values()).map(item -> new SelectOption(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}
}
