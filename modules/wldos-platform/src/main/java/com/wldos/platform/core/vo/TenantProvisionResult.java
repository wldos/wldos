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
 * 租户入驻编排结果。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "租户入驻结果")
@Getter
@Setter
public class TenantProvisionResult {

	@ApiModelProperty(value = "新公司 id")
	private Long comId;

	@ApiModelProperty(value = "租户体系 id")
	private Long archId;

	@ApiModelProperty(value = "首站 domain id")
	private Long domainId;

	@ApiModelProperty(value = "租户管理端入口 URL")
	private String adminUrl;

	@ApiModelProperty(value = "创建或指定的租户管理员用户 id")
	private Long tenantAdminUserId;

	@ApiModelProperty(value = "首管登录账号")
	private String tenantAdminLoginName;

	@ApiModelProperty(value = "附加提示（如已有租户管）")
	private String message;
}
