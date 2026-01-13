/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import com.wldos.common.enums.EncryptionAlgorithm;
import com.wldos.common.enums.EncryptionMode;

/**
 * 统一加密工具类（新格式，支持AES 128/256位和SM4）
 * 
 * 数据格式：
 * [版本号(1字节)] + [算法标识(1字节)] + [密钥长度(1字节)] + [模式标识(1字节)] + [IV长度(1字节)] + [IV数据] + [密文数据]
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public class UnifiedEncryptUtils {

	private UnifiedEncryptUtils() {
	}

	// 版本号
	private static final byte VERSION_1 = 0x01;

	// 算法标识
	private static final byte ALGORITHM_AES = 0x01;
	private static final byte ALGORITHM_SM4 = 0x02;

	// AES密钥长度标识
	private static final byte AES_KEY_128 = (byte) 0x80; // 128位 = 16字节
	private static final byte AES_KEY_192 = (byte) 0xC0; // 192位 = 24字节
	private static final byte AES_KEY_256 = 0x00; // 256位 = 32字节
	private static final byte SM4_KEY_IGNORE = (byte) 0xFF; // SM4固定128位，此字段忽略

	// 模式标识
	private static final byte MODE_ECB = 0x01;
	private static final byte MODE_CBC = 0x02;
	private static final byte MODE_GCM = 0x03;

	private static final int IV_LENGTH = 16; // IV固定16字节
	private static final SecureRandom random = new SecureRandom();

	/**
	 * 统一加密方法（新格式，带版本标识）
	 * 
	 * @param plaintext 明文
	 * @param key 密钥（Hex格式，如果不是Hex格式会自动转换）
	 * @param algorithm 算法（AES或SM4）
	 * @param mode 模式（ECB、CBC或GCM）
	 * @param aesKeySize AES密钥长度（128、192或256，仅AES有效）
	 * @return 加密后的Base64字符串
	 */
	public static String encrypt(String plaintext, String key, EncryptionAlgorithm algorithm, EncryptionMode mode,
			int aesKeySize) {
		return encrypt(plaintext, key, algorithm, mode, aesKeySize, false);
	}

	/**
	 * 统一加密方法（新格式，带版本标识）
	 * 
	 * @param plaintext 明文
	 * @param key 密钥（Hex格式，如果不是Hex格式会自动转换）
	 * @param algorithm 算法（AES或SM4）
	 * @param mode 模式（ECB、CBC或GCM）
	 * @param aesKeySize AES密钥长度（128、192或256，仅AES有效）
	 * @param useDeterministicIV 是否使用确定性IV（用于密码加密，确保相同密码生成相同结果）
	 * @return 加密后的Base64字符串
	 */
	public static String encrypt(String plaintext, String key, EncryptionAlgorithm algorithm, EncryptionMode mode,
			int aesKeySize, boolean useDeterministicIV) {
		if (plaintext == null || key == null) {
			throw new IllegalArgumentException("明文和密钥不能为空");
		}

		// 如果密钥不是Hex格式，自动转换为Hex格式
		key = normalizeKey(key, algorithm, aesKeySize);

		// 1. 根据算法和模式加密
		byte[] encrypted;
		byte[] iv = null;

		if (algorithm == EncryptionAlgorithm.AES) {
			// 验证AES密钥长度
			if (aesKeySize != 128 && aesKeySize != 192 && aesKeySize != 256) {
				throw new IllegalArgumentException("AES密钥长度必须是128、192或256位");
			}

			if (mode == EncryptionMode.CBC) {
				// 使用确定性IV（用于密码加密）或随机IV
				iv = useDeterministicIV ? generateIVFromPassword(plaintext, key) : generateIV();
				encrypted = AesEncryptUtils.encryptCBC(plaintext, key, iv, aesKeySize);
			}
			else if (mode == EncryptionMode.ECB) {
				encrypted = AesEncryptUtils.encryptECB(plaintext, key, aesKeySize);
			}
			else {
				throw new IllegalArgumentException("AES不支持GCM模式");
			}
		}
		else if (algorithm == EncryptionAlgorithm.SM4) {
			if (mode == EncryptionMode.CBC) {
				// 使用确定性IV（用于密码加密）或随机IV
				iv = useDeterministicIV ? generateIVFromPassword(plaintext, key) : generateIV();
				// Sm4EncryptUtils.encryptCBC 返回包含IV的Base64字符串，需要提取密文部分
				String encryptedWithIV = Sm4EncryptUtils.encryptCBC(plaintext, key, iv);
				byte[] encryptedData = Base64.decodeBase64(encryptedWithIV);
				// 提取密文（跳过IV部分）
				encrypted = new byte[encryptedData.length - IV_LENGTH];
				System.arraycopy(encryptedData, IV_LENGTH, encrypted, 0, encrypted.length);
			}
			else if (mode == EncryptionMode.GCM) {
				// 使用确定性IV（用于密码加密）或随机IV
				iv = useDeterministicIV ? generateIVFromPassword(plaintext, key) : generateIV();
				// Sm4EncryptUtils.encryptGCM 返回包含IV的Base64字符串，需要提取密文部分
				String encryptedWithIV = Sm4EncryptUtils.encryptGCM(plaintext, key, iv);
				byte[] encryptedData = Base64.decodeBase64(encryptedWithIV);
				// 提取密文（跳过IV部分）
				encrypted = new byte[encryptedData.length - IV_LENGTH];
				System.arraycopy(encryptedData, IV_LENGTH, encrypted, 0, encrypted.length);
			}
			else {
				throw new IllegalArgumentException("SM4不支持ECB模式");
			}
		}
		else {
			throw new IllegalArgumentException("不支持的算法: " + algorithm);
		}

		// 2. 构建带版本标识的数据
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			output.write(VERSION_1); // 版本号
			output.write(getAlgorithmByte(algorithm)); // 算法标识

			// 密钥长度标识
			if (algorithm == EncryptionAlgorithm.AES) {
				output.write(getAesKeySizeByte(aesKeySize)); // AES密钥长度
			}
			else {
				output.write(SM4_KEY_IGNORE); // SM4固定128位
			}

			output.write(getModeByte(mode)); // 模式标识

			if (iv != null) {
				output.write((byte) iv.length); // IV长度
				output.write(iv); // IV数据
			}
			else {
				output.write((byte) 0); // 无IV（ECB模式）
			}

			output.write(encrypted); // 密文

			// 3. Base64编码
			return Base64.encodeBase64String(output.toByteArray());
		}
		catch (IOException e) {
			throw new EncryptionException("构建加密数据失败", e);
		}
	}

	/**
	 * 统一解密方法（仅支持新格式）
	 * 
	 * @param ciphertext 密文（Base64编码）
	 * @param key 密钥（Hex格式，如果不是Hex格式会自动转换）
	 * @return 解密后的明文
	 */
	public static String decrypt(String ciphertext, String key) {
		if (ciphertext == null || key == null) {
			throw new IllegalArgumentException("密文和密钥不能为空");
		}

		byte[] data = Base64.decodeBase64(ciphertext);

		// 检查版本号
		if (data.length < 5) {
			throw new DecryptionException("密文格式错误：数据长度不足");
		}

		byte version = data[0];
		if (version != VERSION_1) {
			throw new DecryptionException("不支持的版本号: " + version);
		}

		// 解析版本标识
		return decryptV1(data, key);
	}

	/**
	 * 解密新格式（V1）
	 */
	private static String decryptV1(byte[] data, String key) {
		if (data.length < 5) {
			throw new DecryptionException("密文格式错误：数据长度不足");
		}

		byte algorithmByte = data[1];
		byte keySizeFlag = data[2];
		byte mode = data[3];
		byte ivLength = data[4];

		// 确定算法和密钥长度
		EncryptionAlgorithm algorithm;
		int aesKeySize = 128; // 默认值
		if (algorithmByte == ALGORITHM_AES) {
			algorithm = EncryptionAlgorithm.AES;
			aesKeySize = getAesKeySizeFromFlag(keySizeFlag);
		}
		else if (algorithmByte == ALGORITHM_SM4) {
			algorithm = EncryptionAlgorithm.SM4;
		}
		else {
			throw new DecryptionException("不支持的算法标识: " + algorithmByte);
		}

		// 规范化密钥格式
		key = normalizeKey(key, algorithm, aesKeySize);

		// 提取IV
		byte[] iv = null;
		int offset = 5;
		if (ivLength > 0) {
			if (data.length < offset + ivLength) {
				throw new DecryptionException("密文格式错误：IV数据不完整");
			}
			iv = new byte[ivLength];
			System.arraycopy(data, offset, iv, 0, ivLength);
			offset += ivLength;
		}

		// 提取密文
		if (data.length <= offset) {
			throw new DecryptionException("密文格式错误：密文数据为空");
		}
		byte[] encrypted = new byte[data.length - offset];
		System.arraycopy(data, offset, encrypted, 0, encrypted.length);

		// 根据算法和模式解密
		if (algorithm == EncryptionAlgorithm.AES) {
			if (mode == MODE_CBC && iv != null) {
				return AesEncryptUtils.decryptCBC(encrypted, key, iv, aesKeySize);
			}
			else if (mode == MODE_ECB) {
				return AesEncryptUtils.decryptECB(encrypted, key, aesKeySize);
			}
			else {
				throw new DecryptionException("AES不支持此模式: " + mode);
			}
		}
		else if (algorithm == EncryptionAlgorithm.SM4) {
			if (mode == MODE_CBC && iv != null) {
				return Sm4EncryptUtils.decryptCBC(encrypted, key, iv);
			}
			else if (mode == MODE_GCM && iv != null) {
				return Sm4EncryptUtils.decryptGCM(encrypted, key, iv);
			}
			else {
				throw new DecryptionException("SM4不支持此模式: " + mode);
			}
		}
		else {
			throw new DecryptionException("不支持的算法: " + algorithm);
		}
	}

	// ========== 工具方法 ==========

	/**
	 * 生成IV（16字节）
	 * 使用随机数生成，每次调用都不同
	 */
	private static byte[] generateIV() {
		byte[] iv = new byte[IV_LENGTH];
		random.nextBytes(iv);
		return iv;
	}

	/**
	 * 从密码和密钥生成固定IV（用于密码加密，确保相同密码生成相同IV）
	 * 
	 * @param password 密码明文
	 * @param key 加密密钥
	 * @return 固定IV（16字节）
	 */
	private static byte[] generateIVFromPassword(String password, String key) {
		try {
			java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
			// 使用密码和密钥作为盐值，确保相同输入生成相同IV
			String salt = password + key;
			byte[] hash = digest.digest(salt.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			byte[] iv = new byte[IV_LENGTH];
			// 取前16字节作为IV
			System.arraycopy(hash, 0, iv, 0, IV_LENGTH);
			return iv;
		}
		catch (Exception e) {
			throw new EncryptionException("生成IV失败", e);
		}
	}

	/**
	 * 获取算法标识字节
	 */
	private static byte getAlgorithmByte(EncryptionAlgorithm algorithm) {
		if (algorithm == EncryptionAlgorithm.AES) {
			return ALGORITHM_AES;
		}
		else if (algorithm == EncryptionAlgorithm.SM4) {
			return ALGORITHM_SM4;
		}
		throw new IllegalArgumentException("不支持的算法: " + algorithm);
	}

	/**
	 * 获取AES密钥长度标识字节
	 */
	private static byte getAesKeySizeByte(int keySize) {
		if (keySize == 128) {
			return AES_KEY_128;
		}
		else if (keySize == 192) {
			return AES_KEY_192;
		}
		else if (keySize == 256) {
			return AES_KEY_256;
		}
		throw new IllegalArgumentException("不支持的AES密钥长度: " + keySize);
	}

	/**
	 * 从标识字节获取AES密钥长度
	 */
	private static int getAesKeySizeFromFlag(byte flag) {
		if (flag == AES_KEY_128) {
			return 128;
		}
		else if (flag == AES_KEY_192) {
			return 192;
		}
		else if (flag == AES_KEY_256) {
			return 256;
		}
		throw new DecryptionException("不支持的AES密钥长度标识: " + flag);
	}

	/**
	 * 获取模式标识字节
	 */
	private static byte getModeByte(EncryptionMode mode) {
		if (mode == EncryptionMode.ECB) {
			return MODE_ECB;
		}
		else if (mode == EncryptionMode.CBC) {
			return MODE_CBC;
		}
		else if (mode == EncryptionMode.GCM) {
			return MODE_GCM;
		}
		throw new IllegalArgumentException("不支持的模式: " + mode);
	}

	/**
	 * 规范化密钥格式
	 * 如果密钥不是Hex格式，自动转换为Hex格式
	 * 
	 * @param key 原始密钥
	 * @param algorithm 加密算法
	 * @param aesKeySize AES密钥长度（仅AES有效）
	 * @return Hex格式的密钥
	 */
	private static String normalizeKey(String key, EncryptionAlgorithm algorithm, int aesKeySize) {
		// 检查是否为有效的Hex字符串
		if (EncryptionKeyUtils.isValidHex(key)) {
			// 验证密钥长度
			if (algorithm == EncryptionAlgorithm.SM4) {
				if (!EncryptionKeyUtils.isValidSm4Key(key)) {
					// 如果不是32字符，转换为SM4密钥
					return EncryptionKeyUtils.stringToSm4Key(key);
				}
			}
			else if (algorithm == EncryptionAlgorithm.AES) {
				if (!EncryptionKeyUtils.isValidAesKey(key, aesKeySize)) {
					// 如果长度不匹配，转换为AES密钥
					return EncryptionKeyUtils.stringToAesKey(key, aesKeySize);
				}
			}
			return key;
		}
		else {
			// 不是Hex格式，转换为Hex格式
			if (algorithm == EncryptionAlgorithm.SM4) {
				return EncryptionKeyUtils.stringToSm4Key(key);
			}
			else if (algorithm == EncryptionAlgorithm.AES) {
				return EncryptionKeyUtils.stringToAesKey(key, aesKeySize);
			}
			else {
				throw new IllegalArgumentException("不支持的加密算法: " + algorithm);
			}
		}
	}

}
