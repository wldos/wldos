/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 点选图形人机校验提交参数。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "点选人机校验参数")
@Getter
@Setter
public class ClickCaptchaVerifyParams implements Serializable {

	@ApiModelProperty(value = "安全验证编号（点选弹窗打开时由系统下发，用户无感）", required = true)
	@NotBlank(message = "安全验证已失效，请关闭后重新获取邮箱验证码")
	private String challengeUuid;

	@ApiModelProperty(value = "文字按钮模式：按顺序点选的文字")
	private List<String> clicked;

	@ApiModelProperty(value = "背景图模式：按顺序点击的坐标（相对原图）")
	private List<ClickCaptchaPointParams> clicks;
}
