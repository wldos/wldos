/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
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
 * @author 树悉猿
 * @date 2022/2/11
 * @version 1.0
 */
public final class ByteCodeSign {

	public static String messageDigestBase64(CharSequence plaintext) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] digest = messageDigest.digest(plaintext.toString().getBytes(StandardCharsets.UTF_8));
		return new String(Base64.encodeBase64(digest));
	}

	public static String messageDigestBase64(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] digest = messageDigest.digest(bytes);
		return new String(Base64.encodeBase64(digest));
	}

	public static byte[] messageDigest256(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		return messageDigest.digest(bytes);
	}

	public static byte[] md5128(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		return messageDigest.digest(bytes);
	}

	public static String md5(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] digest = messageDigest.digest(bytes);
		return new String(Base64.encodeBase64(digest));
	}

	private ByteCodeSign() {
		throw new IllegalStateException("Utility class");
	}
}