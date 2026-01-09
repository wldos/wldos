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
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 密保问题修改参数。
 *
 * @author 元悉宇宙
 * @date 2022/5/21
 * @version 1.0
 */
@ApiModel(description = "密保问题修改参数")
@Getter
@Setter
public class SecQuestModifyParams implements ModifyParams {
	@ApiModelProperty(value = "用户ID", required = true, example = "1")
	@NotNull(message = "用户ID不能为空")
	private Long id;

	@ApiModelProperty(value = "原密保问题", example = "我的出生地是哪里？")
	private String oldSecQuest;

	@ApiModelProperty(value = "新密保问题", required = true, example = "我的小学名称是什么？")
	@NotBlank(message = "新密保问题不能为空")
	@Size(min = 1, max = 200, message = "密保问题长度必须在1-200之间")
	private String secQuest;

	@Override
	public String getOld() {
		return this.oldSecQuest;
	}

	@Override
	public String getNew() {
		return this.secQuest;
	}
}
