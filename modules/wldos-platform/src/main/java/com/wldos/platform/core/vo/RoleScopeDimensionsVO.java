/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.ArrayList;
import java.util.List;

import com.wldos.platform.core.entity.WoScopeDimension;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * 角色管理范围维度清单（全局字段 + 定制字段）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色管理范围维度清单")
@Getter
@Setter
public class RoleScopeDimensionsVO {

	@ApiModelProperty("全局字段：组织/区划 + scope_ui_tier=global 的 enabled DICT")
	private List<WoScopeDimension> globalDimensions = new ArrayList<>();

	@ApiModelProperty("定制字段：本角色 effective adopt 且非全局的枚举类型")
	private List<ScopeExtensionDimVO> customDimensions = new ArrayList<>();

}

