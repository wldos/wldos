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
 * 插槽绑定：把业务插槽绑定到租户/站点自建的分群或角色；未绑定则按插槽平台默认角色解析。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Table("wo_slot_binding")
@Getter
@Setter
@RbacRowScope(comId = true, domainId = true)
public class WoSlotBinding {
	@Id
	private Long id;

	private String slotCode;

	private Long comId;

	private Long domainId;

	/** 目标类型：role / org */
	private String targetType;

	private Long targetId;

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
