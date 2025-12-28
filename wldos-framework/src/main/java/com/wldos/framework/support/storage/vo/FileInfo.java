/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 文件信息。
 *
 * @author 元悉宇宙
 * @date 2021/5/30
 * @version 1.0
 */
@Getter
@Setter
public class FileInfo {
	private Long id;

	private String name;

	private String path;

	private String url;

	private String mimeType;

	public FileInfo(Long id, String name, String path, String url, String mimeType) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.url = url;
		this.mimeType = mimeType;
	}
}