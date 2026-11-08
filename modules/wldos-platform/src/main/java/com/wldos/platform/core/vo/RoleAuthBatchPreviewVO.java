/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色授权批量推送预览结果。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色授权批量推送预览")
@Getter
@Setter
public class RoleAuthBatchPreviewVO {

	@ApiModelProperty(value = "源角色 id")
	private Long sourceRoleId;

	@ApiModelProperty(value = "源角色编码")
	private String roleCode;

	@ApiModelProperty(value = "源授权资源数")
	private int sourceCount;

	@ApiModelProperty(value = "各站 diff")
	private List<RoleAuthBatchPreviewItem> items = new ArrayList<>();

	@ApiModelProperty(value = "推送前须携带的预览令牌（与当前源/目标/模式绑定，单次推送有效）")
	private String previewToken;
}
