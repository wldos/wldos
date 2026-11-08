/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 站点应用/资源挂载显隐（保留绑定行，仅切换 wo_domain_app / wo_domain_resource.is_valid）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("站点挂载显隐")
@Getter
@Setter
public class DomainMountVisibilityBody {

	@ApiModelProperty(value = "站点 id", required = true)
	private Long domainId;

	@ApiModelProperty(value = "平台资源 id（wo_resource.id）列表，与 appIds 二选一")
	private List<String> resourceIds;

	@ApiModelProperty(value = "应用 id 列表，与 resourceIds 二选一")
	private List<String> appIds;

	@ApiModelProperty(value = "true=显示，false=隐藏", required = true)
	private Boolean visible;
}
