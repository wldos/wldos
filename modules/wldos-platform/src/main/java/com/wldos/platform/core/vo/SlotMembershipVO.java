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

/**
 * 插槽成员判定结果（当前登录用户）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("插槽成员判定")
public class SlotMembershipVO {

	@ApiModelProperty("插槽编码")
	private String slotCode;

	@ApiModelProperty("当前用户是否匹配插槽目标")
	private boolean matched;

	public String getSlotCode() {
		return slotCode;
	}

	public void setSlotCode(String slotCode) {
		this.slotCode = slotCode;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}
}
