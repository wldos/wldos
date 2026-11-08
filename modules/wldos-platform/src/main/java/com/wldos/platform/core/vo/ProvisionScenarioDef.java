/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.github.wldos.platform.support.domain.ProvisionCloneOptions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户入驻场景定义。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "租户入驻场景")
@Getter
@Setter
public class ProvisionScenarioDef {

	@ApiModelProperty(value = "场景编码", required = true, example = "standard")
	private String code;

	@ApiModelProperty(value = "展示名称", required = true, example = "标准 SaaS")
	private String label;

	@ApiModelProperty(value = "模板站 domainId", required = true)
	private Long templateDomainId;

	@ApiModelProperty(value = "克隆选项")
	private ProvisionCloneOptions cloneOptions;

	@ApiModelProperty(value = "是否默认场景")
	private boolean defaultScenario;

	@ApiModelProperty(value = "是否在入驻下拉展示")
	private boolean enabled = true;

	@ApiModelProperty(value = "站点交接落地相对路径（如 /admin、/），供入驻编排使用")
	@JsonAlias("trialHandoffLandingPath")
	private String handoffLandingPath;
}
