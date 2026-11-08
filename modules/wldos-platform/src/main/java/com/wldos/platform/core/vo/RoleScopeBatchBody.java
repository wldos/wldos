/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 批量将源角色管理范围应用到多个目标角色。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("批量应用角色管理范围")
@Getter
@Setter
public class RoleScopeBatchBody {

	@ApiModelProperty(value = "源角色 id", required = true)
	private Long sourceRoleId;

	@ApiModelProperty(value = "目标角色 id 列表", required = true)
	private List<Long> targetRoleIds = new ArrayList<>();
}
