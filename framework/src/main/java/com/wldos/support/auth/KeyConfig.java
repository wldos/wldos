/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.stereotype.Component;

/**
 * RSA密钥配置。
 *
 * @author 树悉猿
 * @date 2023/4/15
 * @version 1.0
 */
@Component("keyConfig")
public final class KeyConfig {
	private byte[] userPubKey;
	private byte[] userPriKey;

	public KeyConfig() {}

	private KeyConfig(byte[] userPubKey, byte[] userPriKey) {
		this.userPubKey = userPubKey;
		this.userPriKey = userPriKey;
	}

	public KeyConfig of(byte[] userPubKey, byte[] userPriKey) {
		return new KeyConfig(userPubKey, userPriKey);
	}

	public byte[] getUserPubKey() {
		return userPubKey;
	}

	public void setUserPubKey(byte[] userPubKey) {
		this.userPubKey = userPubKey;
	}

	public byte[] getUserPriKey() {
		return userPriKey;
	}

	public void setUserPriKey(byte[] userPriKey) {
		this.userPriKey = userPriKey;
	}

	public static String toHexString(byte[] b) {
		return (Base64.getEncoder()).encodeToString(b);
	}

	public static final byte[] toBytes(String s) throws IOException {
		return (Base64.getDecoder()).decode(s);
	}

	@Override
	public String toString() {
		return "KeyConfig{" +
				"userPubKey=" + Arrays.toString(userPubKey) +
				", userPriKey=" + Arrays.toString(userPriKey) +
				'}';
	}

	public void push(byte[] pubs, byte[] pris) {
		this.userPubKey = pubs;
		this.userPriKey = pris;
	}
}
