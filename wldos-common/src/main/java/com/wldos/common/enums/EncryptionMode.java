/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.enums;

/**
 * 加密模式枚举
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public enum EncryptionMode {
	/**
	 * ECB模式（电子密码本模式）
	 */
	ECB("ECB", "电子密码本模式，不推荐使用"),
	
	/**
	 * CBC模式（密码块链接模式，适用于短文本）
	 */
	CBC("CBC", "密码块链接模式，适用于短文本"),
	
	/**
	 * GCM模式（Galois/Counter模式，适用于长文本，提供认证加密）
	 */
	GCM("GCM", "Galois/Counter模式，适用于长文本，提供认证加密");
	
	private final String mode;
	private final String description;
	
	EncryptionMode(String mode, String description) {
		this.mode = mode;
		this.description = description;
	}
	
	public String getMode() {
		return mode;
	}
	
	public String getDescription() {
		return description;
	}
}
