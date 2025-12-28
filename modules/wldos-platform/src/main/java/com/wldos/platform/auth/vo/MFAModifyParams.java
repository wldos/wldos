/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import com.wldos.platform.auth.model.ModifyParams;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * MFA设备修改参数。
 *
 * @author 元悉宇宙
 * @date 2022/5/21
 * @version 1.0
 */
@ApiModel(description = "MFA设备修改参数")
@Getter
@Setter
public class MFAModifyParams implements ModifyParams {
	@ApiModelProperty(value = "用户ID", required = true, example = "1")
	@NotNull(message = "用户ID不能为空")
	private Long id;

	@ApiModelProperty(value = "原MFA设备", example = "old-device-id")
	private String oldMFA;

	@ApiModelProperty(value = "新MFA设备", required = true, example = "new-device-id")
	@NotBlank(message = "新MFA设备不能为空")
	@Size(min = 1, max = 100, message = "MFA设备长度必须在1-100之间")
	private String mfa;

	@Override
	public String getOld() {
		return this.oldMFA;
	}

	@Override
	public String getNew() {
		return this.mfa;
	}
}
