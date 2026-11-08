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
 * 主体认证维护请求体。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("主体认证维护请求体")
@Getter
@Setter
public class SubjectAuthBody {

	@ApiModelProperty("认证id（移除时必传）")
	private Long id;

	@ApiModelProperty("主体类型id（添加时必传）")
	private Long subjectTypeId;

	@ApiModelProperty("用户id（添加时必传）")
	private Long userId;

	@ApiModelProperty("租户id（超管可指定；租户管理员固定本租户）")
	private Long comId;

	@ApiModelProperty("归属站点id（可选；不填为租户级）")
	private Long domainId;
}
