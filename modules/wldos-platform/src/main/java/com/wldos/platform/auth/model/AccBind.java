/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 账号绑定信息。数据保存到第三方账号关联表。
 *
 * @author 元悉宇宙
 * @date 2021/9/22
 * @version 1.0
 */
@ApiModel(description = "账号绑定信息，数据保存到第三方账号关联表")
@Getter
@Setter
public class AccBind {
	@ApiModelProperty(value = "淘宝账号", example = "tb_account")
	private String tb;

	@ApiModelProperty(value = "支付宝账号", example = "zfb_account")
	private String zfb;

	@ApiModelProperty(value = "钉钉账号", example = "dd_account")
	private String dd;

	@ApiModelProperty(value = "微信账号", example = "wx_account")
	private String wx;

	@ApiModelProperty(value = "抖音账号", example = "dy_account")
	private String dy;

	@ApiModelProperty(value = "QQ账号", example = "qq_account")
	private String qq;

	@ApiModelProperty(value = "微博账号", example = "wb_account")
	private String wb;
}
