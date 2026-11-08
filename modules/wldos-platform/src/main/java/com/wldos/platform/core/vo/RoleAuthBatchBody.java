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
 * 会员角色授权批量推送请求。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("角色授权批量推送")
@Getter
@Setter
public class RoleAuthBatchBody {

	@ApiModelProperty(value = "源角色 id（与 sourceDomainId+roleCode 二选一）")
	private Long sourceRoleId;

	@ApiModelProperty(value = "源站点 id（与 sourceRoleId 二选一时配合 roleCode）")
	private Long sourceDomainId;

	@ApiModelProperty(value = "角色编码（sourceRoleId 为空时必填）")
	private String roleCode;

	@ApiModelProperty(value = "目标站点 id 列表", required = true)
	private List<Long> targetDomainIds = new ArrayList<>();

	@ApiModelProperty(value = "模式：addMissing | merge | replace", required = true, example = "addMissing")
	private String mode;

	@ApiModelProperty(value = "replace 模式二次确认", example = "false")
	private Boolean confirmReplace;

	@ApiModelProperty(value = "预览接口返回的 previewToken，推送前必填")
	private String previewToken;
}
