/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 背景图点选坐标（相对原图像素）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "点选坐标")
@Getter
@Setter
public class ClickCaptchaPointParams implements Serializable {

	@ApiModelProperty(value = "横坐标（像素）", required = true, example = "120")
	private int x;

	@ApiModelProperty(value = "纵坐标（像素）", required = true, example = "64")
	private int y;
}
