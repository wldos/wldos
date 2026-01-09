/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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

@ApiModel(description = "域应用信息")
@Getter
@Setter
public class DomainApp {
	@ApiModelProperty(value = "应用ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "应用名称", example = "内容管理")
	private String appName;

	@ApiModelProperty(value = "应用描述", example = "内容管理系统")
	private String appDesc;

	@ApiModelProperty(value = "书籍状态", example = "1")
	private int bookStatus;
}
