/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.encrypt;

import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import com.wldos.common.enums.EncryptionAlgorithm;

/**
 * 加密密钥生成和验证工具类
 * 
 * 提供生成符合要求的随机加密密钥的方法
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public class EncryptionKeyUtils {

	private EncryptionKeyUtils() {
	}

	/** 安全随机数生成器 */
	private static final SecureRandom random = new SecureRandom();

	/** Hex字符串格式验证正则表达式（只包含0-9、a-f、A-F） */
	private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");

	/**
	 * 生成SM4加密密钥（128位，32个十六进制字符）
	 * 
	 * SM4算法要求：
	 * - 密钥长度：128位（16字节）
	 * - 格式：Hex字符串（32个字符）
	 * 
	 * @return Hex格式的密钥（32个字符）
	 * 
	 * @example
	 * <pre>
	 * String key = EncryptionKeyUtils.generateSm4Key();
	 * // 输出示例: "a1b2c3d4e5f6789012345678901234ab"
	 * </pre>
	 */
	public static String generateSm4Key() {
		byte[] key = new byte[16]; // 128位 = 16字节
		random.nextBytes(key);
		return Hex.encodeHexString(key);
	}

	/**
	 * 生成AES加密密钥（Hex格式）
	 * 
	 * @param keySize 密钥长度（128、192或256位）
	 * @return Hex格式的密钥
	 * 
	 * @example
	 * <pre>
	 * // 生成128位密钥（32个Hex字符）
	 * String key128 = EncryptionKeyUtils.generateAesKey(128);
	 * 
	 * // 生成256位密钥（64个Hex字符）
	 * String key256 = EncryptionKeyUtils.generateAesKey(256);
	 * </pre>
	 */
	public static String generateAesKey(int keySize) {
		if (keySize != 128 && keySize != 192 && keySize != 256) {
			throw new IllegalArgumentException("AES密钥长度必须是128、192或256位");
		}
		byte[] key = new byte[keySize / 8];
		random.nextBytes(key);
		return Hex.encodeHexString(key);
	}

	/**
	 * 生成AES 128位密钥（32个十六进制字符）
	 * 
	 * @return Hex格式的密钥
	 */
	public static String generateAes128Key() {
		return generateAesKey(128);
	}

	/**
	 * 生成AES 256位密钥（64个十六进制字符）
	 * 
	 * @return Hex格式的密钥
	 */
	public static String generateAes256Key() {
		return generateAesKey(256);
	}

	/**
	 * 根据加密算法生成密钥
	 * 
	 * @param algorithm 加密算法
	 * @param aesKeySize AES密钥长度（仅当算法为AES时有效，128、192或256）
	 * @return Hex格式的密钥
	 */
	public static String generateKey(EncryptionAlgorithm algorithm, int aesKeySize) {
		if (algorithm == EncryptionAlgorithm.SM4) {
			return generateSm4Key();
		}
		else if (algorithm == EncryptionAlgorithm.AES) {
			return generateAesKey(aesKeySize);
		}
		else {
			throw new IllegalArgumentException("不支持的加密算法: " + algorithm);
		}
	}

	/**
	 * 验证Hex字符串格式
	 * 
	 * @param hex Hex字符串
	 * @return 是否为有效的Hex字符串
	 */
	public static boolean isValidHex(String hex) {
		if (hex == null || hex.isEmpty()) {
			return false;
		}
		return HEX_PATTERN.matcher(hex).matches();
	}

	/**
	 * 验证SM4密钥格式
	 * 
	 * SM4密钥要求：
	 * - 必须是Hex格式（只包含0-9、a-f、A-F）
	 * - 长度必须是32个字符（128位）
	 * 
	 * @param key 密钥
	 * @return 是否为有效的SM4密钥
	 */
	public static boolean isValidSm4Key(String key) {
		if (key == null || key.length() != 32) {
			return false;
		}
		return isValidHex(key);
	}

	/**
	 * 验证AES密钥格式
	 * 
	 * @param key 密钥
	 * @param keySize 密钥长度（128、192或256位）
	 * @return 是否为有效的AES密钥
	 */
	public static boolean isValidAesKey(String key, int keySize) {
		if (key == null) {
			return false;
		}
		int expectedLength = keySize / 4; // 每4位Hex字符表示1字节
		if (key.length() != expectedLength) {
			return false;
		}
		return isValidHex(key);
	}

	/**
	 * 验证AES 128位密钥格式
	 * 
	 * @param key 密钥
	 * @return 是否为有效的AES 128位密钥
	 */
	public static boolean isValidAes128Key(String key) {
		return isValidAesKey(key, 128);
	}

	/**
	 * 验证AES 256位密钥格式
	 * 
	 * @param key 密钥
	 * @return 是否为有效的AES 256位密钥
	 */
	public static boolean isValidAes256Key(String key) {
		return isValidAesKey(key, 256);
	}

	/**
	 * 将字符串转换为Hex格式密钥（通过SHA-256哈希）
	 * 
	 * 如果配置的密钥不是Hex格式，可以使用此方法将任意字符串转换为Hex格式
	 * 
	 * @param input 输入字符串
	 * @param keySize 目标密钥长度（字节数，SM4为16，AES128为16，AES256为32）
	 * @return Hex格式的密钥
	 * 
	 * @example
	 * <pre>
	 * // 将配置的字符串转换为SM4密钥
	 * String configKey = "yourEncodeKeyForPassword";
	 * String hexKey = EncryptionKeyUtils.stringToHexKey(configKey, 16);
	 * </pre>
	 */
	public static String stringToHexKey(String input, int keySize) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("输入字符串不能为空");
		}
		if (keySize <= 0 || keySize > 64) {
			throw new IllegalArgumentException("密钥长度必须在1-64字节之间");
		}

		try {
			// 使用SHA-256哈希，然后截取指定长度
			java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			
			// 如果需要的长度小于32字节，截取；如果大于32字节，重复哈希
			byte[] key = new byte[keySize];
			if (keySize <= 32) {
				System.arraycopy(hash, 0, key, 0, keySize);
			}
			else {
				// 对于大于32字节的情况，使用PBKDF2或多次哈希
				System.arraycopy(hash, 0, key, 0, 32);
				// 剩余部分使用再次哈希
				for (int i = 32; i < keySize; i += 32) {
					hash = digest.digest(hash);
					int copyLength = Math.min(32, keySize - i);
					System.arraycopy(hash, 0, key, i, copyLength);
				}
			}
			
			return Hex.encodeHexString(key);
		}
		catch (Exception e) {
			throw new EncryptionException("字符串转Hex密钥失败", e);
		}
	}

	/**
	 * 将字符串转换为SM4密钥（16字节，32个Hex字符）
	 * 
	 * @param input 输入字符串
	 * @return Hex格式的SM4密钥
	 */
	public static String stringToSm4Key(String input) {
		return stringToHexKey(input, 16);
	}

	/**
	 * 将字符串转换为AES密钥
	 * 
	 * @param input 输入字符串
	 * @param keySize 密钥长度（128、192或256位）
	 * @return Hex格式的AES密钥
	 */
	public static String stringToAesKey(String input, int keySize) {
		return stringToHexKey(input, keySize / 8);
	}

	/**
	 * 打印密钥生成示例（用于配置参考）
	 *
	 * === 加密密钥生成工具 ===
	 *
	 * 1. SM4 密钥（128位，32个Hex字符）：
	 *    bacb2de80df6b1cf67d4717be7d35bba
	 *    配置示例: wldos.encryption.encode-key=bacb2de80df6b1cf67d4717be7d35bba
	 *
	 * 2. AES 128位密钥（32个Hex字符）：
	 *    d7103f58f07aab9dd41396f0f3926cd2
	 *    配置示例: wldos.encryption.encode-key=d7103f58f07aab9dd41396f0f3926cd2
	 *
	 * 3. AES 256位密钥（64个Hex字符）：
	 *    383700b981f8447005cffaadffb955bb568f22fdd38ff8e367267b8807d07e43
	 *    配置示例: wldos.encryption.encode-key=383700b981f8447005cffaadffb955bb568f22fdd38ff8e367267b8807d07e43
	 *
	 * 4. 字符串转Hex密钥示例：
	 *    输入: yourEncodeKeyForPassword
	 *    输出: 8d7c75892addeb39c463bd3565bb6920
	 *    配置示例: wldos.encryption.encode-key=8d7c75892addeb39c463bd3565bb6920
	 *
	 * 5. 密钥验证示例：
	 *    bacb2de80df6b1cf67d4717be7d35bba 是有效的SM4密钥: true
	 *    yourEncodeKeyForPassword 是有效的Hex字符串: false
	 *
	 * @param args 命令行参数（可选）
	 */
	public static void main(String[] args) {
		System.out.println("=== 加密密钥生成工具 ===\n");
		
		System.out.println("1. SM4 密钥（128位，32个Hex字符）：");
		String sm4Key = generateSm4Key();
		System.out.println("   " + sm4Key);
		System.out.println("   配置示例: wldos.encryption.encode-key=" + sm4Key);
		System.out.println();
		
		System.out.println("2. AES 128位密钥（32个Hex字符）：");
		String aes128Key = generateAes128Key();
		System.out.println("   " + aes128Key);
		System.out.println("   配置示例: wldos.encryption.encode-key=" + aes128Key);
		System.out.println();
		
		System.out.println("3. AES 256位密钥（64个Hex字符）：");
		String aes256Key = generateAes256Key();
		System.out.println("   " + aes256Key);
		System.out.println("   配置示例: wldos.encryption.encode-key=" + aes256Key);
		System.out.println();
		
		System.out.println("4. 字符串转Hex密钥示例：");
		String input = "yourEncodeKeyForPassword";
		String hexKey = stringToSm4Key(input);
		System.out.println("   输入: " + input);
		System.out.println("   输出: " + hexKey);
		System.out.println("   配置示例: wldos.encryption.encode-key=" + hexKey);
		System.out.println();
		
		System.out.println("5. 密钥验证示例：");
		System.out.println("   " + sm4Key + " 是有效的SM4密钥: " + isValidSm4Key(sm4Key));
		System.out.println("   " + input + " 是有效的Hex字符串: " + isValidHex(input));
	}
}
