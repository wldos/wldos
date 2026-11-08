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
 * 管理端首页（/admin Tab）解析结果：域级/根域/全局/builtin 四级兜底。
 */
@ApiModel(description = "管理端首页配置")
@Getter
@Setter
public class AdminHomeConfigVO {

	@ApiModelProperty(value = "模块类型：builtin、component、redirect", example = "component")
	private String module;

	@ApiModelProperty(value = "组件路径（module=component）或跳转路径（module=redirect）", example = "admin/home/examples/commercial-overview")
	private String url;

	@ApiModelProperty(value = "扩展属性 JSON 字符串", example = "{}")
	private String extraProps;

	@ApiModelProperty(value = "首页 Tab 标题（可选）", example = "运营总览")
	private String title;

	@ApiModelProperty(value = "生效来源：domain、rootDomain、global、builtin", example = "rootDomain")
	private String source;

	@ApiModelProperty(value = "实际提供配置的域 ID；global/builtin 时为 null")
	private Long resolvedDomainId;

	@ApiModelProperty(value = "本次解析请求的域 ID")
	private Long requestDomainId;
}
