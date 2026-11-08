/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户入驻编排入参：公司 + 首站 + 租户管。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel(description = "租户入驻请求")
@Getter
@Setter
public class TenantProvisionBody {

	@ApiModelProperty(value = "公司编码", required = true, example = "tenant-b")
	private String comCode;

	@ApiModelProperty(value = "公司名称", required = true, example = "租户 B")
	private String comName;

	@ApiModelProperty(value = "公司描述")
	private String comDesc;

	@ApiModelProperty(value = "体系编码", example = "arch-b")
	private String archCode;

	@ApiModelProperty(value = "体系名称", example = "体系 B")
	private String archName;

	@ApiModelProperty(value = "主站域名", required = true, example = "b.example.com")
	private String siteDomain;

	@ApiModelProperty(value = "主站名称", example = "租户 B 主站")
	private String siteName;

	@ApiModelProperty(value = "主站访问 URL（高级：填写后优先于端口/协议组装）", example = "https://b.example.com")
	private String siteUrl;

	@ApiModelProperty(value = "访问端口（80/443 写入 URL 时省略端口）", example = "8088")
	private Integer sitePort;

	@ApiModelProperty(value = "访问协议", example = "http", allowableValues = "http, https")
	private String siteScheme;

	@ApiModelProperty(value = "创建首管账号（与 tenantAdminUserIds 互斥）")
	private TenantAdminAccountBody tenantAdminAccount;

	@ApiModelProperty(value = "指定已有用户为租户管理员（与 tenantAdminAccount 互斥）")
	private List<String> tenantAdminUserIds;

	@ApiModelProperty(value = "首站站点管理员用户 ID（可选）")
	private Long domainAdminUserId;

	@ApiModelProperty(value = "入驻场景编码（空则使用默认场景）", example = "standard")
	private String provisionScenarioCode;
}
