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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 站点新建 nav_dir 响应。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "站点新建菜单分组结果")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteNavDirResult {

	@ApiModelProperty(value = "新建 wo_resource.id")
	private Long resourceId;

	@ApiModelProperty(value = "本站 wo_domain_resource.id")
	private Long domainResourceId;
}
