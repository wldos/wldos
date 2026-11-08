/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.config;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.wldos.platform.core.dict.DictRegistry;
import com.wldos.platform.core.enums.ProvisionScenarioCodeEnum;
import io.github.wldos.common.vo.SelectOption;

import org.springframework.stereotype.Component;

/**
 * 平台字典：入驻场景编码（Term 优先，枚举回退）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class PlatformProvisionDictRegistrar {

	@PostConstruct
	public void register() {
		DictRegistry.registerEnum("provisionScenarioCode", "入驻场景编码", () -> Arrays.stream(ProvisionScenarioCodeEnum.values())
				.map(item -> SelectOption.of(item.getLabel(), item.getValue()))
				.collect(Collectors.toList()));
		DictRegistry.registerTermBinding("provisionScenarioCode", "provision_scenario");
	}
}
