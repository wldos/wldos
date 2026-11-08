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
 * 用户角色四来源视图：组织角色、直挂角色、主体角色、身份角色。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@ApiModel("用户角色四来源")
@Getter
@Setter
public class UserRoleSourceVO {

	@ApiModelProperty("组织角色（经归属组织挂接）")
	private List<UserOrgRoleVO> orgRoles;

	@ApiModelProperty("直挂角色（wo_role_binding target_type=user）")
	private List<UserOrgRoleVO.RoleItem> directRoles;

	@ApiModelProperty("主体角色（经主体认证 + 主体授权）")
	private List<UserOrgRoleVO.RoleItem> subjectRoles;

	@ApiModelProperty("身份角色（经身份登记册所属身份组挂接）")
	private List<UserOrgRoleVO.RoleItem> identityRoles;
}
