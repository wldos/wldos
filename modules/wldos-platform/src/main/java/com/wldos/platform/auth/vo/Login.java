/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import java.util.List;

import com.wldos.framework.support.auth.vo.Token;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录后返回用户认证和权限信息。
 *
 * @author 元悉宇宙
 * @date 2021-04-30
 * @version V1.0
 */
@ApiModel(description = "登录后返回用户认证和权限信息")
@Getter
@Setter
public class Login {
	@ApiModelProperty(value = "登录状态：ok=成功, error=失败", example = "ok")
	private String status;

	@ApiModelProperty(value = "错误信息或提示信息", example = "登录成功")
	private String news;

	@ApiModelProperty(value = "访问token和刷新token")
	private Token token;

	@ApiModelProperty(value = "用户的权限列表，默认排除菜单权限")
	private List<String> currentAuthority;

	@ApiModelProperty(value = "登录类型：account=账号密码, mobile=手机验证码", example = "account")
	private String type;
}