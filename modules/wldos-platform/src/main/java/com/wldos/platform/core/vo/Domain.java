/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import com.wldos.common.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "域名信息")
@Getter
@Setter
public class Domain {

	@ApiModelProperty(value = "站点域名", example = "example.com")
	private String siteDomain;

	@ApiModelProperty(value = "站点名称", example = "示例站点")
	private String siteName;

	@ApiModelProperty(value = "站点URL", example = "https://example.com")
	private String siteUrl;

	@ApiModelProperty(value = "站点Logo", example = "/logo.png")
	private String siteLogo;

	@ApiModelProperty(value = "网站图标", example = "/favicon.ico")
	private String favicon;

	@ApiModelProperty(value = "站点标题", example = "示例站点 - 标题")
	private String siteTitle;

	@ApiModelProperty(value = "站点关键词", example = "示例,站点,关键词")
	private String siteKeyword;

	@ApiModelProperty(value = "站点描述", example = "这是一个示例站点")
	private String siteDescription;

	@ApiModelProperty(value = "页脚信息", example = "© 2024 示例站点")
	private String foot;

	@ApiModelProperty(value = "友情链接", example = "链接信息")
	private String flink;

	@ApiModelProperty(value = "版权信息", example = "版权所有")
	private String copy;

	@ApiModelProperty(value = "平台版本号", example = Constants.WLDOS_VERSION)
	private String version;
}
