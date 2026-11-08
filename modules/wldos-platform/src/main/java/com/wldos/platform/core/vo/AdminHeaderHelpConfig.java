/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */

package com.wldos.platform.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 管理端顶栏「使用文档」配置（全局 wo_options + 域级 wo_domain_option 覆盖）。
 */
@ApiModel(description = "管理端顶栏使用文档配置")
@Getter
@Setter
public class AdminHeaderHelpConfig {

	@ApiModelProperty("是否显示使用文档入口")
	private boolean enabled;

	@ApiModelProperty("文档链接 URL，为空时不展示")
	private String url;
}
