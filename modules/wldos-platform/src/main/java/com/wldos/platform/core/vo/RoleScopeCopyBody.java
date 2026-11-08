/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 从源角色复制管理范围。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("复制角色管理范围")
@Getter
@Setter
public class RoleScopeCopyBody {

	@ApiModelProperty(value = "源角色 id", required = true)
	private Long sourceRoleId;

	@ApiModelProperty(value = "目标角色 id", required = true)
	private Long targetRoleId;
}
