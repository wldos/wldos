/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 新增「我的常用」请求体。
 * {@code title} 在路径以 {@code /admin} 开头时可由服务端按管理菜单解析。
 */
@Getter
@Setter
public class MyShortcutCreateBody {

	@Size(max = 128)
	private String title;

	@NotBlank
	@Size(max = 512)
	private String path;

	private Long resourceId;

	@Size(max = 256)
	private String icon;
}
