/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
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
 * @author 元悉宇宙
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
