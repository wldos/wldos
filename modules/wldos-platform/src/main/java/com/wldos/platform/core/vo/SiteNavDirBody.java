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
 * 站点自建导航目录（nav_dir）请求体。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "站点新建菜单分组")
@Getter
@Setter
public class SiteNavDirBody {

	@ApiModelProperty(value = "站点 ID", required = true)
	private Long domainId;

	@ApiModelProperty(value = "资源 ID；更新/删除时必填")
	private Long resourceId;

	@ApiModelProperty(value = "分组展示名", required = true)
	private String label;

	@ApiModelProperty(value = "本站 overlay 上级 resourceId；留空挂根")
	private Long overlayParentId;

	@ApiModelProperty(value = "本站 overlay 排序")
	private Integer overlayDisplayOrder;
}
