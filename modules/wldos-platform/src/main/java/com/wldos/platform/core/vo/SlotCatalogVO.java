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
 * 插槽注册表条目（含当前租户是否已绑定）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("插槽注册表条目")
@Getter
@Setter
public class SlotCatalogVO {

	@ApiModelProperty("插槽编码")
	private String slotCode;

	@ApiModelProperty("目标类型：role/org/role_set")
	private String targetType;

	@ApiModelProperty("默认实现 Kind 编码")
	private String defaultKind;

	@ApiModelProperty("插槽说明")
	private String description;

	@ApiModelProperty("当前租户是否已绑定")
	private boolean bound;
}
