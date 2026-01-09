/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 密码修改参数。
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@ApiModel(description = "密码修改参数")
@Getter
@Setter
public class PasswdModifyParams {
	@ApiModelProperty(value = "用户ID", required = true, example = "1")
	@NotNull(message = "用户ID不能为空")
	private Long id;

	@ApiModelProperty(value = "原密码", required = true, example = "oldPassword123")
	@NotBlank(message = "原密码不能为空")
	private String oldPasswd;

	@ApiModelProperty(value = "新密码", required = true, example = "newPassword123")
	@NotBlank(message = "新密码不能为空")
	@Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
	private String password;

	@ApiModelProperty(value = "确认密码", required = true, example = "newPassword123")
	@NotBlank(message = "确认密码不能为空")
	private String confirm;
}
