/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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


@ApiModel("遗漏 API 检查项")
@Getter
@Setter
public class ResourceApiCoverageItemVO {

	@ApiModelProperty("来源页面菜单路径")
	private String sourceMenuPath;

	@ApiModelProperty("来源页面菜单名称")
	private String sourceMenuName;

	@ApiModelProperty("页面依赖的 API 路径")
	private String apiPath;

	@ApiModelProperty("页面依赖的 API 方法")
	private String apiMethod;

	@ApiModelProperty("API 名称（Controller @ApiOperation）")
	private String apiName;

	@ApiModelProperty("说明")
	private String message;

	@ApiModelProperty("来源 menu 资源 id")
	private Long sourceMenuId;

	@ApiModelProperty("应用 id")
	private Long appId;

	@ApiModelProperty("来源 menu 资源类型")
	private String sourceMenuResourceType;

	@ApiModelProperty("建议新建的 button 资源类型")
	private String suggestedResourceType;

	@ApiModelProperty("建议资源名称")
	private String suggestedResourceName;

	@ApiModelProperty("建议资源编码")
	private String suggestedResourceCode;
}
