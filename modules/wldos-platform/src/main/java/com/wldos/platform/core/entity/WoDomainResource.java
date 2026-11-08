/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.entity;


import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.github.wldos.platform.support.rbac.RbacRowScope;
import lombok.Getter;
import lombok.Setter;

@Table
@Getter
@Setter
@RbacRowScope(comId = false, domainId = true)
public class WoDomainResource {
	@Id
	private Long id;

	private String moduleName;

	private Long resourceId;

	private Long appId;

	private Long termTypeId;

	private String url;

	/** 域资源扩展 JSON，下发至 DynSet.extraProps（如门户静态首页 portalStatic） */
	@Column("extra_props")
	private String extraProps;

	/** 本站侧栏父节点；NULL 继承 wo_resource.parent_id */
	@Column("overlay_parent_id")
	private Long overlayParentId;

	/** 本站排序；NULL 继承 wo_resource.display_order */
	@Column("overlay_display_order")
	private Integer overlayDisplayOrder;

	/** 本站展示名；NULL 继承 resource_name */
	@Column("overlay_label")
	private String overlayLabel;

	private Long domainId;

	private String isValid;

	private Long createBy;

	private Timestamp createTime;

	private String createIp;

	private Long updateBy;

	private Timestamp updateTime;

	private String updateIp;

	private String deleteFlag;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer versions;
}
