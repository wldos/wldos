/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.ArrayList;
import java.util.List;

import com.wldos.platform.core.entity.WoRoleScope;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色管理范围保存体（scope 取值 + 本角色直接选用的扩展维）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色管理范围保存")
@Getter
@Setter
public class RoleScopeSaveBody {

	@ApiModelProperty("各维 scope 取值")
	private List<WoRoleScope> scopes = new ArrayList<>();

	@ApiModelProperty("本角色直接选用的扩展维 dictCode（不含继承自父）")
	private List<String> adoptedDictCodes = new ArrayList<>();

	@ApiModelProperty("兼容：dimension_code 形式 direct adopt")
	private List<String> adoptedDimensionCodes = new ArrayList<>();
}
