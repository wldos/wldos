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
 * 角色定制字段回显（枚举类型 + 已解析 dimension_code）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色定制字段")
@Getter
@Setter
public class ScopeExtensionDimVO {

	@ApiModelProperty("枚举类型 dictCode")
	private String dictCode;

	@ApiModelProperty("枚举类型展示名")
	private String dictName;

	@ApiModelProperty("catalog dimension_code")
	private String dimensionCode;

	@ApiModelProperty("是否继承自父角色")
	private Boolean inherited;
}
