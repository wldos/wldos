/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 密码重置参数。
 *
 * @author Yuanxi Universe
 * @date 2022/9/25
 * @version 1.0
 */
@ApiModel(description = "密码重置参数")
@Getter
@Setter
public class PasswdResetParams {
	@ApiModelProperty(value = "登录用户名", required = true, example = "admin")
	@NotBlank(message = "登录名不能为空")
	private String loginName;

	@ApiModelProperty(value = "验证码", required = true, example = "123456")
	@NotBlank(message = "验证码不能为空")
	private String captcha;

	@ApiModelProperty(value = "验证码UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
	@NotBlank(message = "验证码标识不能为空")
	private String uuid;

	@ApiModelProperty(value = "找回密码发码凭证", required = true)
	@NotBlank(message = "请先获取邮箱验证码")
	private String forgetProof;

	@ApiModelProperty(value = "状态", hidden = true)
	private String status;

	@ApiModelProperty(value = "新密码", required = true, example = "newPassword123")
	@NotBlank(message = "密码不能为空")
	@Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
	private String password;

	@ApiModelProperty(value = "确认密码", required = true, example = "newPassword123")
	@NotBlank(message = "确认密码不能为空")
	private String confirm;

	@ApiModelProperty(value = "邮箱地址", required = true, example = "user@example.com")
	@NotBlank(message = "邮箱不能为空")
	@Email(message = "邮箱格式不正确")
	private String email;
}
