/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
 * 角色资源授权树。
 *
 * @author 元悉宇宙
 * @date 2021/5/22
 * @version 1.0
 */
@ApiModel(description = "角色资源授权树")
@Getter
@Setter
public class RoleResTree {
	@ApiModelProperty(value = "已授权资源列表")
	private List<RoleRes> roleRes;

	@ApiModelProperty(value = "当前角色资源授权树镜像")
	private List<AuthRes> authRes;

	@Override
	public String toString() {
		return "{roleRes: " + roleRes.toString() + ", authRes: " + authRes.toString() + "}";
	}
}
