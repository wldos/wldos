/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import com.wldos.platform.auth.model.ModifyParams;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 备用邮箱修改参数。
 *
 * @author 元悉宇宙
 * @date 2022/5/21
 * @version 1.0
 */
@ApiModel(description = "备用邮箱修改参数")
@Getter
@Setter
public class BakEmailModifyParams implements ModifyParams {
	@ApiModelProperty(value = "用户ID", required = true, example = "1")
	@NotNull(message = "用户ID不能为空")
	private Long id;

	@ApiModelProperty(value = "原备用邮箱", example = "old@example.com")
	private String oldBakEmail;

	@ApiModelProperty(value = "新备用邮箱", required = true, example = "new@example.com")
	@NotBlank(message = "新备用邮箱不能为空")
	@Email(message = "备用邮箱格式不正确")
	private String bakEmail;

	@Override
	public String getOld() {
		return this.oldBakEmail;
	}

	@Override
	public String getNew() {
		return this.bakEmail;
	}
}
