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
 * 平台运营菜单固定 scope（operator com + 主站 domain）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("平台运营 scope")
@Getter
@Setter
public class PlatformOperatorContextVO {

	@ApiModelProperty("是否已配置且校验通过")
	private boolean ready;

	@ApiModelProperty("平台运营 wo_company.id")
	private Long operatorComId;

	@ApiModelProperty("官网主站 wo_domain.id")
	private Long primaryDomainId;

	@ApiModelProperty("公司名称")
	private String comName;

	@ApiModelProperty("主站域名")
	private String siteDomain;

	@ApiModelProperty("主站名称")
	private String siteName;
}
