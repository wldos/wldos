/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "角色ID和编码")
@Getter
@Setter
public class RoleIdCode {

	@ApiModelProperty(value = "角色ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "角色编码", example = "admin")
	private String roleCode;
}
