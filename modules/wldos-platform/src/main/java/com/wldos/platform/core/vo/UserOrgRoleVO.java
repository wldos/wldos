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
 * 用户归属组织及该组织授予的角色（权限查看「归属与角色」页签）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("用户归属组织及其角色")
@Getter
@Setter
public class UserOrgRoleVO {

	@ApiModelProperty("组织 id")
	private Long orgId;

	@ApiModelProperty("组织名称")
	private String orgName;

	@ApiModelProperty("组织编码")
	private String orgCode;

	@ApiModelProperty("体系 id")
	private Long archId;

	@ApiModelProperty("归属租户 id")
	private Long comId;

	@ApiModelProperty("该组织授予的角色")
	private List<RoleItem> roles;

	@ApiModel("组织授予的角色项")
	@Getter
	@Setter
	public static class RoleItem {

		@ApiModelProperty("角色 id")
		private Long roleId;

		@ApiModelProperty("角色名称")
		private String roleName;

		@ApiModelProperty("角色编码")
		private String roleCode;
	}
}
