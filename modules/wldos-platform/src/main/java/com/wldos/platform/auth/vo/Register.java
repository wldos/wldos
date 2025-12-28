/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
 * 注册信息。
 *
 * @author 元悉宇宙
 * @date 2021/4/30
 * @version 1.0
 */
@ApiModel(description = "用户注册参数")
@Getter
@Setter
public class Register {
	@ApiModelProperty(value = "用户ID", hidden = true)
	private long id;

	@ApiModelProperty(value = "登录名（默认邮箱）", required = true, example = "user@example.com")
	private String loginName;

	@ApiModelProperty(value = "昵称（平台上显示名称）", required = true, example = "张三")
	@NotBlank(message = "昵称不能为空")
	@Size(min = 1, max = 50, message = "昵称长度必须在1-50之间")
	private String nickname;

	@ApiModelProperty(value = "邮箱地址", required = true, example = "user@example.com")
	@NotBlank(message = "邮箱不能为空")
	@Email(message = "邮箱格式不正确")
	private String email;

	@ApiModelProperty(value = "密码", required = true, example = "password123")
	@NotBlank(message = "密码不能为空")
	@Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
	private String passwd;

	@ApiModelProperty(value = "手机号", example = "13800138000")
	@Size(min = 11, max = 11, message = "手机号必须是11位数字")
	private String mobile;

	@ApiModelProperty(value = "注册IP", hidden = true)
	private String registerIp;

	@ApiModelProperty(value = "验证码", example = "1234abc-def-1234")
	private String verifyCode;

	@ApiModelProperty(value = "确认密码", required = true, example = "password123")
	@NotBlank(message = "确认密码不能为空")
	private String confirm;

	@ApiModelProperty(value = "前缀", hidden = true)
	private String prefix;

	public Register() {
	}

	public static Register of(Long uid, String loginName, String nickname, String registerIp) {
		return new Register(uid, loginName, nickname, registerIp);
	}

	private Register(Long uid, String loginName, String nickname, String registerIp) {
		this.id = uid;
		this.loginName = loginName;
		this.nickname = nickname;
		this.registerIp = registerIp;
	}
}
