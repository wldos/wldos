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
 * 插槽绑定列表行。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("插槽绑定列表行")
@Getter
@Setter
public class SlotBindingVO {

	@ApiModelProperty("绑定id")
	private Long id;

	@ApiModelProperty("插槽编码")
	private String slotCode;

	@ApiModelProperty("插槽说明")
	private String slotDescription;

	@ApiModelProperty("目标类型：role/org")
	private String targetType;

	@ApiModelProperty("租户id")
	private Long comId;

	@ApiModelProperty("站点id（空为租户级绑定）")
	private Long domainId;

	@ApiModelProperty("站点名")
	private String domainName;

	@ApiModelProperty("绑定目标id")
	private Long targetId;

	@ApiModelProperty("绑定目标名称（分群/角色名）")
	private String targetName;
}
