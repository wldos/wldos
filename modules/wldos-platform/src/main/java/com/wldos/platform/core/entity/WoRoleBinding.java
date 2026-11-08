/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import io.github.wldos.platform.support.rbac.RbacRowScope;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色多态绑定：角色挂到组织（org）、人（user）、身份登记册组（identity）、主体类型（subject_type）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Table("wo_role_binding")
@Getter
@Setter
@RbacRowScope(comId = true, domainId = true)
public class WoRoleBinding {

	/** 绑定目标类型：org / user / identity / subject_type */
	public static final String TARGET_ORG = "org";
	public static final String TARGET_USER = "user";
	public static final String TARGET_IDENTITY = "identity";
	public static final String TARGET_SUBJECT_TYPE = "subject_type";

	@Id
	private Long id;

	private Long roleId;

	private String targetType;

	/** 目标id：orgId / userId / 身份组 wo_org id / subjectTypeId */
	private Long targetId;

	private Long comId;

	private Long domainId;

	/** org 通道兼容键（对齐 wo_role_org.arch_id），其余通道恒 0 */
	private Long archId;

	private String isValid;

	private Long createBy;

	private String createIp;

	private Timestamp createTime;

	private Long updateBy;

	private String updateIp;

	private Timestamp updateTime;

	private String deleteFlag;

	@Version
	private Integer versions;
}
