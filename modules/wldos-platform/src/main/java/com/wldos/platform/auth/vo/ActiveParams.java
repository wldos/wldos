/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 激活参数。
 *
 * @author 元悉宇宙
 * @date 2022/9/14
 * @version 1.0
 */
@ApiModel(description = "账号激活参数")
@Getter
@Setter
public class ActiveParams {

	@ApiModelProperty(value = "激活验证码", required = true, example = "abc123def456")
	@NotBlank(message = "激活验证码不能为空")
	private String verify;
}
