/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.enums;

/**
 * 加密算法类型枚举
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public enum EncryptionAlgorithm {
	/**
	 * AES加密算法
	 */
	AES("AES", "AES加密算法"),
	
	/**
	 * 国密SM4加密算法
	 */
	SM4("SM4", "国密SM4加密算法");
	
	private final String algorithm;
	private final String description;
	
	EncryptionAlgorithm(String algorithm, String description) {
		this.algorithm = algorithm;
		this.description = description;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}
	
	public String getDescription() {
		return description;
	}
}
