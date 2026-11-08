/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 个人中心「类别」只读标签（仅 curAccount 本人可见）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "个人中心类别标签")
@Getter
@Setter
public class AccountCategoryTag {

	@ApiModelProperty(value = "来源：membership=会员分组, identity=身份登记, subject=主体, org=组织角色", example = "identity")
	private String kind;

	@ApiModelProperty(value = "展示文案", example = "租户管理员")
	private String label;

	@ApiModelProperty(value = "稳定键（roleCode/orgId 等，前端 key 用）", example = "tadmin")
	private String code;
}
