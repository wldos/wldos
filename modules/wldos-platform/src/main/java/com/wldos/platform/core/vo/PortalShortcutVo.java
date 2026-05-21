/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 门户「我的常用」对外展示。
 */
@Getter
@Setter
public class PortalShortcutVo {

	private Long id;

	private String title;

	private String path;

	private String icon;

	private Long resourceId;

	private Integer displayOrder;

	private Integer hitCount;

	/** 最近一次访问时间（毫秒时间戳，便于前端展示） */
	private Long lastHitAtMs;

	/** 是否置顶 */
	private Boolean pinned;
}
