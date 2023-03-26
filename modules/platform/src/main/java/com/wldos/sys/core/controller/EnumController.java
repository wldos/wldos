/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.common.vo.SelectOption;
import com.wldos.support.resource.enums.ResourceEnum;
import com.wldos.support.web.enums.TemplateTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 常用枚举controller。
 *
 * @author 树悉猿
 * @date 2022/3/7
 * @version 1.0
 */
@RequestMapping("enum")
@RestController
public class EnumController {
	/**
	 * 模板类型枚举
	 *
	 * @return 枚举列表
	 */
	@GetMapping("select/template")
	public List<SelectOption> fetchEnumTempType() {

		return Arrays.stream(TemplateTypeEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}

	/**
	 * 资源类型枚举
	 *
	 * @return 枚举列表
	 */
	@GetMapping("select/resource")
	public List<SelectOption> fetchEnumResType() {
		return Arrays.stream(ResourceEnum.values()).map(item -> SelectOption.of(item.getLabel(), item.getValue())).collect(Collectors.toList());
	}
}
