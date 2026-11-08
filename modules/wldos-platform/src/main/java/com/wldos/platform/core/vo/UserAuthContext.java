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
 * 用户权限查询时使用的域与租户解析结果。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "用户权限解析上下文")
@Getter
@Setter
public class UserAuthContext {

	@ApiModelProperty(value = "权限解析域 ID")
	private Long domainId;

	@ApiModelProperty(value = "权限解析租户 ID")
	private Long comId;

	@ApiModelProperty(value = "权限解析域名称")
	private String domainName;

	@ApiModelProperty(value = "权限解析租户名称")
	private String comName;
}
