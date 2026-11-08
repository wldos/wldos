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
 * 同 Kind 角色实例与基准授权差异只读项。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色授权差异对比项")
@Getter
@Setter
public class RoleAuthDriftItem {

	@ApiModelProperty(value = "站点 id")
	private Long domainId;

	@ApiModelProperty(value = "站点名称")
	private String domainName;

	@ApiModelProperty(value = "角色实例 id")
	private Long roleId;

	@ApiModelProperty(value = "实例授权数")
	private int instanceCount;

	@ApiModelProperty(value = "相对基准缺失")
	private int missingFromBaseline;

	@ApiModelProperty(value = "相对基准多余")
	private int extraVsBaseline;

	@ApiModelProperty(value = "本站是否已有该角色实例（可对比）")
	private boolean instanceReady = true;

	@ApiModelProperty(value = "不可对比时的说明")
	private String statusNote;
}
