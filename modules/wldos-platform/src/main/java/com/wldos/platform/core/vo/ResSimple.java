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
 * 按模板新建菜单。
 *
 * @author 元悉宇宙
 * @date 2022/3/8
 * @version 1.0
 */
@ApiModel(description = "资源模板信息")
@Getter
@Setter
public class ResSimple {

	@ApiModelProperty(value = "资源名称", example = "用户管理", required = true)
	private String resName;

	@ApiModelProperty(value = "模板类型", example = "info")
	private String tempType;

	@ApiModelProperty(value = "分类类型ID", example = "1")
	private Long termTypeId;

	@ApiModelProperty(value = "图标", example = "user")
	private String icon;

	@ApiModelProperty(value = "请求方法", example = "GET")
	private String reqMethod;

	@ApiModelProperty(value = "打开方式", example = "_self")
	private String target;

	@ApiModelProperty(value = "应用ID", example = "1")
	private Long appId;

	@ApiModelProperty(value = "父资源ID", example = "0")
	private Long parentId;

	@ApiModelProperty(value = "备注", example = "用户管理菜单")
	private String remark;
}
