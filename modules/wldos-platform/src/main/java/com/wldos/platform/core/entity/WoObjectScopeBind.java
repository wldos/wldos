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
 * 对象范围维绑定（{@code wo_object_scope_bind}）。
 *
 * @author Yuanxi Universe
 * @version 1.0.0
 * @since 3.0
 */
@Table("wo_object_scope_bind")
@Getter
@Setter
public class WoObjectScopeBind extends BaseEntity {
	private String entityKey;
	private String dimensionCode;
	private String columnName;
	private String joinTemplate;
}
