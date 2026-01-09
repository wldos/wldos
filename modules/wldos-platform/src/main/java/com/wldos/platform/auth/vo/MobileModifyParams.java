/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import com.wldos.platform.auth.model.ModifyParams;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 密保手机修改参数。
 *
 * @author 元悉宇宙
 * @date 2021/4/29
 * @version 1.0
 */
@ApiModel(description = "密保手机修改参数")
@Getter
@Setter
public class MobileModifyParams implements ModifyParams {
	@ApiModelProperty(value = "用户ID", required = true, example = "1")
	@NotNull(message = "用户ID不能为空")
	private Long id;

	@ApiModelProperty(value = "原手机号", example = "13800138000")
	private String oldMobile;

	@ApiModelProperty(value = "新手机号", required = true, example = "13800138001")
	@NotBlank(message = "新手机号不能为空")
	@Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
	private String mobile;

	@Override
	public String getOld() {
		return this.oldMobile;
	}

	@Override
	public String getNew() {
		return this.mobile;
	}
}
