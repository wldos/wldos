/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 基于登录参数封装的对象。
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@ApiModel(description = "登录认证参数")
@Getter
@Setter
public class LoginAuthParams implements Serializable {
	@ApiModelProperty(value = "登录用户名", required = true, example = "admin")
	@NotBlank(message = "用户名不能为空")
	@Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
	private String username;

	@ApiModelProperty(value = "密码", required = true, example = "123456")
	@NotBlank(message = "密码不能为空")
	private String password;

	@ApiModelProperty(value = "验证码（图形验证码+uuid）", example = "1234abc-def-1234")
	private String verifyCode;

	@ApiModelProperty(value = "手机号（手机登录时必填）", example = "13800138000")
	@Size(min = 11, max = 11, message = "手机号必须是11位数字")
	private String mobile;

	@ApiModelProperty(value = "手机验证码（手机登录时必填）", example = "123456")
	private String captcha;

	@ApiModelProperty(value = "是否自动登录", example = "false")
	private boolean autoLogin;

	@ApiModelProperty(value = "登录方式：account（账号密码）、mobile（手机验证码）", example = "account")
	private String type;
}
