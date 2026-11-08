/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 找回密码：二次验证通过后的邮箱准入（不发信）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "找回密码邮箱准入")
@Getter
@Setter
public class ForgetEmailAdmissionParams implements Serializable {

	@ApiModelProperty(value = "绑定邮箱", required = true)
	@NotBlank(message = "请输入邮箱")
	@Email(message = "邮箱格式不正确")
	private String email;

	@ApiModelProperty(value = "二次验证通过凭证（仅商业二次验证启用时必填）")
	private String graphPass;
}
