/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.entity;

import com.wldos.framework.mvc.entity.BaseEntity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 门户个人中心「我的常用」持久化条目（按用户 + 租户 + 域隔离）。
 */
@Table
@Getter
@Setter
public class WoUserPortalShortcut extends BaseEntity {

	private Long userId;

	private Long comId;

	private Long domainId;

	private Long archId;

	private Long orgId;

	private String title;

	private String path;

	private Long resourceId;

	private String icon;

	private Integer displayOrder;

	/** 访问次数（菜单历史累计） */
	private Integer hitCount;

	/** 最近一次访问时间 */
	private java.time.LocalDateTime lastHitAt;

	/**
	 * 置顶：'1' 置顶（顺序由 display_order 决定），'0' 或未设置则按访问频率排序。
	 */
	private String pinned;
}
