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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 验证码验证参数。
 *
 * @author 元悉宇宙
 * @date 2021/5/4
 * @version 1.0
 */
@ApiModel(description = "验证码验证参数")
@Getter
@Setter
public class CaptchaVO implements Serializable {
	@ApiModelProperty(value = "验证码", required = true, example = "1234")
	@NotBlank(message = "验证码不能为空")
	private String captcha;

	@ApiModelProperty(value = "验证码UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
	@NotBlank(message = "验证码UUID不能为空")
	private String uuid;

	@ApiModelProperty(value = "状态", hidden = true)
	private String status;
}
