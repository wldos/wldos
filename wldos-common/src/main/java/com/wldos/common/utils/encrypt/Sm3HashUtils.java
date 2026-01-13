/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.encrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;

/**
 * 国密SM3哈希工具类
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public class Sm3HashUtils {

	private Sm3HashUtils() {
	}

	static {
		// 注册BouncyCastle提供者
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	/**
	 * SM3哈希
	 * 
	 * @param data 待哈希的数据
	 * @return Hex格式的哈希值
	 */
	public static String hash(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SM3", "BC");
			byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
			return Hex.toHexString(hash);
		}
		catch (Exception e) {
			throw new EncryptionException("SM3哈希失败", e);
		}
	}

	/**
	 * SM3哈希（字节数组）
	 * 
	 * @param data 待哈希的数据
	 * @return 哈希值字节数组
	 */
	public static byte[] hash(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SM3", "BC");
			return digest.digest(data);
		}
		catch (Exception e) {
			throw new EncryptionException("SM3哈希失败", e);
		}
	}

	/**
	 * SM3哈希（字节数组，返回Hex字符串）
	 * 
	 * @param data 待哈希的数据
	 * @return Hex格式的哈希值
	 */
	public static String hashToHex(byte[] data) {
		byte[] hash = hash(data);
		return Hex.toHexString(hash);
	}

}
