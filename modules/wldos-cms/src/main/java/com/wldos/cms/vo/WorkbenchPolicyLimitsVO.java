/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 创作工作台权益快照（作品 + 可选当前作品章节）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("创作权益快照")
public class WorkbenchPolicyLimitsVO {

	@ApiModelProperty("各 action 快照")
	private List<WorkbenchPolicyLimitItemVO> items = new ArrayList<>();

	public List<WorkbenchPolicyLimitItemVO> getItems() {
		return items;
	}

	public void setItems(List<WorkbenchPolicyLimitItemVO> items) {
		this.items = items;
	}
}
