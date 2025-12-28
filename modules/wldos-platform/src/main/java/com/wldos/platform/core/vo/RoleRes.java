/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
 * 角色授予的资源。
 *
 * @author 元悉宇宙
 * @date 2021/5/21
 * @version 1.0
 */
@ApiModel(description = "角色授予的资源")
@Getter
@Setter
public class RoleRes {
	@ApiModelProperty(value = "资源ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "是否继承", example = "true")
	private boolean inherit;

	@Override
	public String toString() {
		return "{id: " + id.toString() + ", inherit: " + inherit + "}";
	}
}
