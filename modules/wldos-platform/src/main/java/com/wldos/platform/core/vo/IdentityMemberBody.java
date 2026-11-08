/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 身份登记册成员维护入参。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("身份登记册成员维护入参")
@Getter
@Setter
public class IdentityMemberBody {

	@ApiModelProperty(value = "身份类别：admin/tadmin/can_trust（dadmin 请走站点管理绑定入口）", required = true)
	private String kind;

	@ApiModelProperty(value = "用户id列表", required = true)
	private List<String> userIds;

	@ApiModelProperty("受托租户id（kind=tadmin 时必填）")
	private Long userComId;
}
