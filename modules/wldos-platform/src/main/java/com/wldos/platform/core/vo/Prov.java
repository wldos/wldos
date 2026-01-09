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

/**
 * 省分。
 *
 * @author 元悉宇宙
 * @date 2021/6/7
 * @version 1.0
 */
@ApiModel(description = "省份信息")
@Getter
@Setter
public class Prov {
	@ApiModelProperty(value = "省份ID", example = "1")
	private Long id;

	@ApiModelProperty(value = "省份名称", example = "北京市")
	private String name;
}
