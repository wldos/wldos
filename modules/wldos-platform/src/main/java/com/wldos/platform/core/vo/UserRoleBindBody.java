/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 人直挂角色绑定/解绑请求体。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("人直挂角色绑定请求")
@Getter
@Setter
public class UserRoleBindBody {

	@ApiModelProperty(value = "目标用户 id", required = true)
	private Long userId;

	@ApiModelProperty(value = "角色 id", required = true)
	private Long roleId;
}
