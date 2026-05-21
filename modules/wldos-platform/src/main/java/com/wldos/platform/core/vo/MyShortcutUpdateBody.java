/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.vo;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 更新「我的常用」请求体（字段均可选，但至少应在业务层保证有一个变更）。
 */
@Getter
@Setter
public class MyShortcutUpdateBody {

	@Size(max = 128)
	private String title;

	@Size(max = 512)
	private String path;

	private Long resourceId;

	@Size(max = 256)
	private String icon;

	private Integer displayOrder;

	/**
	 * 置顶：true 置顶，false 取消置顶；null 表示不修改。
	 */
	private Boolean pinned;
}
