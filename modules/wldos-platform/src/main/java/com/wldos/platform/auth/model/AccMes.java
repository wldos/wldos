/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 账户消息设置。
 *
 * @author 元悉宇宙
 * @date 2021/9/22
 * @version 1.0
 */
@ApiModel(description = "账户消息设置")
@Getter
@Setter
public class AccMes {
	@ApiModelProperty(value = "用户消息状态：1=启用, 0=禁用", example = "1")
	private String userMesStatus;

	@ApiModelProperty(value = "系统消息状态：1=启用, 0=禁用", example = "1")
	private String sysMesStatus;

	@ApiModelProperty(value = "待办消息状态：1=启用, 0=禁用", example = "1")
	private String todoMesStatus;
}
