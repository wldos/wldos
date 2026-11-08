/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.entity;

import org.springframework.data.relational.core.mapping.Table;

import com.wldos.framework.mvc.entity.BaseEntity;

import io.github.wldos.platform.support.rbac.RbacRowScope;
import lombok.Getter;
import lombok.Setter;

/**
 * 域级系统选项（覆盖 {@code wo_options} 的全局同名 {@code option_key}；未配置时门户读全局）。
 */
@Table("wo_domain_option")
@Getter
@Setter
@RbacRowScope(comId = true, domainId = true)
public class WoDomainOption extends BaseEntity {

	private Long domainId;

	/** 租户公司，与列表 {@code applyTenantFilter} 列名一致 */
	private Long comId;

	private String optionKey;

	private String optionName;

	private String optionValue;

	private String description;

	private String optionType;

	private String appCode;
}
