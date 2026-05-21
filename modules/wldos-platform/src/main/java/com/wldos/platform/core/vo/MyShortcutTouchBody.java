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
 * 门户菜单访问打点：写入或累加「我的常用」访问统计。
 * {@code title} 可为空：路径以 {@code /admin} 开头时由服务端按当前用户管理菜单解析名称与资源 id。
 */
@Getter
@Setter
public class MyShortcutTouchBody {

	@NotBlank
	@Size(max = 512)
	private String path;

	@Size(max = 128)
	private String title;

	private Long resourceId;

	@Size(max = 256)
	private String icon;
}
