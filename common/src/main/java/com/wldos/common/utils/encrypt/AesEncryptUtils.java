/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * AES加/解密工具类。
 *
 * @author 树悉猿
 * @date 2021/7/15
 * @version 1.0
 */
public class AesEncryptUtils {

	private AesEncryptUtils() {
	}

	public static final String KEY_ALGORITHM = "AES";

	public static final int DEFAULT_KEY_SIZE = 128;

	private static final String ECB_PKCS_5_PADDING = "AES/ECB/PKCS5Padding";

	/** 加解密算法/工作模式/填充方式 */
	private static final String ECB_PKCS_7_PADDING = "AES/ECB/PKCS7Padding";

	public static final String ECB_NO_PADDING = "AES/ECB/NoPadding";

	public static String base64Encode(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	public static byte[] base64Decode(String base64Code) {
		return Base64.decodeBase64(base64Code);
	}

	public static byte[] aesEncryptToBytes(String content, String hexAesKey) throws DecoderException {
		return encrypt(content.getBytes(StandardCharsets.UTF_8), Hex.decodeHex(hexAesKey.toCharArray()));
	}

	public static String aesEncrypt(String content, String hexAesKey) throws DecoderException {
		return base64Encode(aesEncryptToBytes(content, hexAesKey));
	}

	public static String aesDecryptByBytes(byte[] encryptBytes, String hexAesKey) throws DecoderException {
		byte[] decrypt = decrypt(encryptBytes, Hex.decodeHex(hexAesKey.toCharArray()));
		return new String(decrypt, StandardCharsets.UTF_8);
	}

	public static String aesDecrypt(String encryptStr, String hexAesKey) throws DecoderException {
		return aesDecryptByBytes(base64Decode(encryptStr), hexAesKey);
	}

	public static String initHexKey() {
		return Hex.encodeHexString(initKey());
	}

	public static byte[] initKey() {
		return initKey(DEFAULT_KEY_SIZE);
	}

	public static byte[] initKey(int keySize) {
		if (keySize != 128 && keySize != 192 && keySize != 256) {
			throw new AesEncException("error keySize: " + keySize);
		}

		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		}
		catch (NoSuchAlgorithmException e) {
			throw new AesEncException("no such algorithm exception: " + KEY_ALGORITHM, e);
		}
		keyGenerator.init(keySize);

		SecretKey secretKey = keyGenerator.generateKey();

		return secretKey.getEncoded();
	}

	private static Key toKey(byte[] key) {
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}

	public static byte[] encrypt(byte[] data, byte[] key) {
		return encrypt(data, key, ECB_PKCS_5_PADDING);
	}

	public static byte[] encrypt(byte[] data, byte[] key, final String cipherAlgorithm) {
		Key k = toKey(key);
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);

			cipher.init(Cipher.ENCRYPT_MODE, k);

			if (ECB_NO_PADDING.equals(cipherAlgorithm)) {
				return cipher.doFinal(formatWithZeroPadding(data, cipher.getBlockSize()));
			}

			return cipher.doFinal(data);
		}
		catch (Exception e) {
			throw new AesEncException("AES encrypt error", e);
		}
	}

	public static byte[] decrypt(byte[] data, byte[] key) {
		return decrypt(data, key, ECB_PKCS_5_PADDING);
	}

	public static byte[] decrypt(byte[] data, byte[] key, final String cipherAlgorithm) {
		Key k = toKey(key);
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, k);

			if (ECB_NO_PADDING.equals(cipherAlgorithm)) {
				return removeZeroPadding(cipher.doFinal(data), cipher.getBlockSize());
			}

			return cipher.doFinal(data);
		}
		catch (Exception e) {
			throw new AesEncException("AES decrypt error", e);
		}
	}

	private static byte[] formatWithZeroPadding(byte[] data, final int blockSize) {
		final int length = data.length;
		final int remainLength = length % blockSize;

		if (remainLength > 0) {
			byte[] inputData = new byte[length + blockSize - remainLength];
			System.arraycopy(data, 0, inputData, 0, length);
			return inputData;
		}
		return data;
	}

	private static byte[] removeZeroPadding(byte[] data, final int blockSize) {
		final int length = data.length;
		final int remainLength = length % blockSize;
		if (remainLength == 0) {
			int i = length - 1;
			while (i >= 0 && 0 == data[i]) {
				i--;
			}
			byte[] outputData = new byte[i + 1];
			System.arraycopy(data, 0, outputData, 0, outputData.length);
			return outputData;
		}
		return data;
	}
}