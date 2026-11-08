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
 * 单条站点菜单 overlay 项。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "站点菜单 overlay 项")
@Getter
@Setter
public class ResTreeOverlayItem {

	@ApiModelProperty(value = "平台资源 ID", required = true)
	private Long resourceId;

	@ApiModelProperty(value = "本站上级（wo_resource.id；0/null 表示挂根）")
	private Long overlayParentId;

	@ApiModelProperty(value = "本站排序")
	private Integer overlayDisplayOrder;

	@ApiModelProperty(value = "站内展示名")
	private String overlayLabel;
}
