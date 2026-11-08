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
 * 入驻场景列表视图（含模板站就绪状态）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "租户入驻场景视图")
@Getter
@Setter
public class ProvisionScenarioView extends ProvisionScenarioDef {

	@ApiModelProperty(value = "模板站域名")
	private String templateSiteDomain;

	@ApiModelProperty(value = "模板站名称")
	private String templateSiteName;

	@ApiModelProperty(value = "模板站域资源挂载数")
	private long resourceCount;

	@ApiModelProperty(value = "模板站应用挂载数")
	private long appCount;

	@ApiModelProperty(value = "模板站挂载是否就绪")
	private boolean ready;

	@ApiModelProperty(value = "场景摘要（复制项说明）")
	private String cloneSummary;
}
