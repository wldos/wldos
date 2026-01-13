/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.wldos.common.enums.EncryptionAlgorithm;
import com.wldos.common.enums.EncryptionMode;

/**
 * 加密算法配置属性
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
@ConfigurationProperties(prefix = "wldos.encryption")
public class EncryptionProperties {

	/**
	 * 默认加密算法类型
	 */
	private EncryptionAlgorithm defaultAlgorithm = EncryptionAlgorithm.AES;

	/**
	 * 密码加密算法（短文本，使用CBC模式）
	 */
	private EncryptionAlgorithm passwordAlgorithm = EncryptionAlgorithm.SM4;

	/**
	 * 长文本加密算法（使用GCM模式）
	 */
	private EncryptionAlgorithm longTextAlgorithm = EncryptionAlgorithm.SM4;

	/**
	 * AES密钥长度（128或256）
	 */
	private int aesKeySize = 256;

	/**
	 * 短文本加密模式（默认CBC）
	 */
	private EncryptionMode shortTextMode = EncryptionMode.CBC;

	/**
	 * 长文本加密模式（默认GCM）
	 */
	private EncryptionMode longTextMode = EncryptionMode.GCM;

	/**
	 * 短文本阈值（小于等于此长度使用短文本模式）
	 */
	private int shortTextThreshold = 32;

	/**
	 * 加密key，可以配置为与token密钥一致
	 */
	private String encodeKey;

	// ========== Getters and Setters ==========

	public EncryptionAlgorithm getDefaultAlgorithm() {
		return defaultAlgorithm;
	}

	public void setDefaultAlgorithm(EncryptionAlgorithm defaultAlgorithm) {
		this.defaultAlgorithm = defaultAlgorithm;
	}

	public EncryptionAlgorithm getPasswordAlgorithm() {
		return passwordAlgorithm;
	}

	public void setPasswordAlgorithm(EncryptionAlgorithm passwordAlgorithm) {
		this.passwordAlgorithm = passwordAlgorithm;
	}

	public EncryptionAlgorithm getLongTextAlgorithm() {
		return longTextAlgorithm;
	}

	public void setLongTextAlgorithm(EncryptionAlgorithm longTextAlgorithm) {
		this.longTextAlgorithm = longTextAlgorithm;
	}

	public int getAesKeySize() {
		return aesKeySize;
	}

	public void setAesKeySize(int aesKeySize) {
		if (aesKeySize != 128 && aesKeySize != 192 && aesKeySize != 256) {
			throw new IllegalArgumentException("AES密钥长度必须是128、192或256位");
		}
		this.aesKeySize = aesKeySize;
	}

	public EncryptionMode getShortTextMode() {
		return shortTextMode;
	}

	public void setShortTextMode(EncryptionMode shortTextMode) {
		this.shortTextMode = shortTextMode;
	}

	public EncryptionMode getLongTextMode() {
		return longTextMode;
	}

	public void setLongTextMode(EncryptionMode longTextMode) {
		this.longTextMode = longTextMode;
	}

	public int getShortTextThreshold() {
		return shortTextThreshold;
	}

	public void setShortTextThreshold(int shortTextThreshold) {
		this.shortTextThreshold = shortTextThreshold;
	}

	public String getEncodeKey() {
		return encodeKey;
	}

	public void setEncodeKey(String encodeKey) {
		this.encodeKey = encodeKey;
	}
}
