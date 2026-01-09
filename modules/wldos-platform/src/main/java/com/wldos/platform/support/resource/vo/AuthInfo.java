/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.resource.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 授权的资源信息。
 *
 * @author 元悉宇宙
 * @date 2021/4/26
 * @version 1.0
 */
@ApiModel(description = "授权的资源信息")
@Getter
@Setter
public class AuthInfo implements Serializable {
	@ApiModelProperty(value = "资源编码", example = "RES001")
	private String resourceCode;

	@ApiModelProperty(value = "资源名称", example = "用户管理")
	private String resourceName;

	@ApiModelProperty(value = "资源路径", example = "/admin/sys/user")
	private String resourcePath;

	@ApiModelProperty(value = "资源类型", example = "menu")
	private String resourceType;

	@ApiModelProperty(value = "请求方法", example = "GET")
	private String requestMethod;
}
