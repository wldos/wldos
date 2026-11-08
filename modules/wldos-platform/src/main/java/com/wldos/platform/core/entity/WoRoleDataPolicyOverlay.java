/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.entity;

import org.springframework.data.relational.core.mapping.Table;

import com.wldos.framework.mvc.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色 data_policy 按站点/租户的覆盖配置。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Table("wo_role_data_policy_overlay")
@Getter
@Setter
public class WoRoleDataPolicyOverlay extends BaseEntity {
	private Long roleId;
	private Long comId;
	private Long domainId;
	private String dataPolicy;
}
