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
 * 单站授权批量推送 diff 预览项。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色授权批量推送预览项")
@Getter
@Setter
public class RoleAuthBatchPreviewItem {

	@ApiModelProperty(value = "目标站点 id")
	private Long domainId;

	@ApiModelProperty(value = "站点名称")
	private String domainName;

	@ApiModelProperty(value = "目标角色实例 id")
	private Long targetRoleId;

	@ApiModelProperty(value = "当前授权资源数")
	private int currentCount;

	@ApiModelProperty(value = "推送后将新增")
	private int toAdd;

	@ApiModelProperty(value = "推送后将移除")
	private int toRemove;

	@ApiModelProperty(value = "推送后总数")
	private int afterCount;

	@ApiModelProperty(value = "目标站是否已有该角色实例（可推送）")
	private boolean instanceReady = true;

	@ApiModelProperty(value = "不可推送时的说明")
	private String statusNote;
}
