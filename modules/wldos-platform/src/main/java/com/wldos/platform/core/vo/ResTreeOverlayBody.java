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
 * 批量保存站点菜单 overlay 请求体。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "站点菜单布局批量保存")
@Getter
@Setter
public class ResTreeOverlayBody {

	@ApiModelProperty(value = "站点 ID", required = true)
	private Long domainId;

	@ApiModelProperty(value = "overlay 项列表", required = true)
	private List<ResTreeOverlayItem> items;
}
