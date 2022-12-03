/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * 字节码签名。
 *
 * @author 树悉猿
 * @date 2022/2/11
 * @version 1.0
 */
public final class ByteCodeSign {
	/**
	 * 给字节码的文本创建16进制签名
	 *
	 * @param plaintext 字节码文本
	 * @return 签名
	 * @throws NoSuchAlgorithmException 算法异常
	 */
	public static String messageDigestBase64(CharSequence plaintext) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] digest = messageDigest.digest(plaintext.toString().getBytes(StandardCharsets.UTF_8));
		return new String(Base64.encodeBase64(digest));
	}

	/**
	 * 给字节码的文本创建16进制签名
	 *
	 * @param bytes 字节码二进制字节
	 * @return 签名
	 * @throws NoSuchAlgorithmException 算法异常
	 */
	public static String messageDigestBase64(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] digest = messageDigest.digest(bytes);
		return new String(Base64.encodeBase64(digest));
	}

	/**
	 * 给字节码的文本创建二进制256bit签名
	 *
	 * @param bytes 字节码二进制字节
	 * @return 签名
	 * @throws NoSuchAlgorithmException 算法异常
	 */
	public static byte[] messageDigest256(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		return messageDigest.digest(bytes);
	}

	/**
	 * 给字节码的文本创建二进制128bit签名
	 *
	 * @param bytes 字节码二进制字节
	 * @return 签名
	 * @throws NoSuchAlgorithmException 算法异常
	 */
	public static byte[] md5128(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		return messageDigest.digest(bytes);
	}

	/**
	 * 给字节码的文本创建md5签名
	 *
	 * @param bytes 字节码二进制字节
	 * @return 签名
	 * @throws NoSuchAlgorithmException 算法异常
	 */
	public static String md5(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] digest = messageDigest.digest(bytes);
		return new String(Base64.encodeBase64(digest));
	}

	private ByteCodeSign() {throw new IllegalStateException("Utility class");}
}
