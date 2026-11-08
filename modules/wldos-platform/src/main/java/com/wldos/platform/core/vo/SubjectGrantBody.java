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
 * 主体授权（主体类型 ↔ 角色）维护请求体。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("主体授权维护请求体")
@Getter
@Setter
public class SubjectGrantBody {

	@ApiModelProperty("授权id（移除时必传）")
	private Long id;

	@ApiModelProperty("主体类型id（添加时必传）")
	private Long subjectTypeId;

	@ApiModelProperty("角色id列表（添加时必传，可多个）")
	private List<Long> roleIds;

	@ApiModelProperty("租户id（超管可指定；租户管理员固定本租户）")
	private Long comId;

	@ApiModelProperty("站点id（站点级授权时传；空为租户级）")
	private Long domainId;
}
