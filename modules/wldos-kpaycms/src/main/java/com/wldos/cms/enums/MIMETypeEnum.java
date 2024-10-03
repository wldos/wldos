/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.cms.enums;

/**
 * MIME类型大类枚举值。
 *
 * @author 元悉宇宙
 * @date 2021/6/13
 * @version 1.0
 */
public enum MIMETypeEnum {
	/** 图片 */
	IMAGE("图片", "image"),
	/** 文本 */
	TEXT("文本", "text"),
	/** 视频 */
	VIDEO("视频", "video"),
	/** 音频 */
	AUDIO("音频", "audio"),
	/** 应用程序 */
	APPLICATION("应用程序", "application");

	private final String label;

	private final String value;

	MIMETypeEnum(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{label: '" + this.label + "', value: '" + this.value + "'}";
	}
}
