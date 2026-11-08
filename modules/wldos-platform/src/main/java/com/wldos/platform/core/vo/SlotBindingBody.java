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
 * 插槽绑定/解绑请求体。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("插槽绑定请求体")
@Getter
@Setter
public class SlotBindingBody {

	@ApiModelProperty("绑定id（解绑时必传）")
	private Long id;

	@ApiModelProperty("插槽编码（绑定时必传）")
	private String slotCode;

	@ApiModelProperty("租户id（超管可指定；租户管理员固定为本租户）")
	private Long comId;

	@ApiModelProperty("站点id（站点级绑定时传；空为租户级）")
	private Long domainId;

	@ApiModelProperty("绑定目标id（wo_org 或 wo_role 主键，绑定时必传）")
	private Long targetId;
}
