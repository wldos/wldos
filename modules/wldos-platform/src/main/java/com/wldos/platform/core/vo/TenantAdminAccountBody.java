/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户入驻时创建首管账号。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "租户入驻-创建首管账号")
@Getter
@Setter
public class TenantAdminAccountBody {

	@ApiModelProperty(value = "登录账号", required = true, example = "tenant_b_admin")
	private String loginName;

	@ApiModelProperty(value = "邮箱", required = true, example = "admin@tenant-b.example.com")
	private String email;

	@ApiModelProperty(value = "昵称", required = true, example = "租户管理员")
	private String nickname;

	@ApiModelProperty(value = "密码", required = true, example = "password123")
	private String passwd;

	@ApiModelProperty(value = "确认密码", required = true, example = "password123")
	private String confirm;
}
