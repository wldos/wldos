/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import com.wldos.common.vo.TreeNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "域资源信息")
@Getter
@Setter
public class DomRes extends TreeNode<DomRes> {

	@ApiModelProperty(value = "资源ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "资源名称", example = "用户管理")
	private String resourceName;

	@ApiModelProperty(value = "是否有效", example = "1")
	private String isValid;

	@ApiModelProperty(value = "备注", example = "用户管理资源")
	private String remark;

	@ApiModelProperty(value = "是否已选择", example = "true")
	private boolean selected;
}
