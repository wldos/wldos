/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;

/**
 * 国密SM4加密工具类
 * 
 * 支持模式：
 * - CBC模式：适用于密码等短文本
 * - GCM模式：适用于长文本，提供认证加密
 * 
 * @author 元悉宇宙
 * @date 2026/01/10
 */
public class Sm4EncryptUtils {

	private Sm4EncryptUtils() {
	}

	/** 密钥算法类型 */
	public static final String KEY_ALGORITHM = "SM4";

	/** 密钥长度（128位） */
	public static final int KEY_SIZE = 128;

	/** GCM模式标签长度 */
	private static final int GCM_TAG_LENGTH = 128;

	/** IV长度（16字节） */
	private static final int IV_LENGTH = 16;

	/** 安全随机数生成器 */
	private static final SecureRandom random = new SecureRandom();

	static {
		// 注册BouncyCastle提供者
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	/**
	 * SM4 CBC模式加密（适用于短文本，如密码）
	 * 
	 * @param plaintext 明文
	 * @param hexKey 密钥（Hex格式，32个字符）
	 * @param iv IV（16字节），如果为null则自动生成
	 * @return 加密后的Base64字符串（包含IV）
	 */
	public static String encryptCBC(String plaintext, String hexKey, byte[] iv) {
		try {
			byte[] key = hexToBytes(hexKey);
			if (iv == null) {
				iv = generateIV();
			}

			SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

			// 将IV和密文组合：IV(16字节) + 密文
			byte[] result = new byte[IV_LENGTH + encrypted.length];
			System.arraycopy(iv, 0, result, 0, IV_LENGTH);
			System.arraycopy(encrypted, 0, result, IV_LENGTH, encrypted.length);

			return Base64.encodeBase64String(result);
		}
		catch (Exception e) {
			throw new EncryptionException("SM4 CBC加密失败", e);
		}
	}

	/**
	 * SM4 CBC模式加密（自动生成IV）
	 * 
	 * @param plaintext 明文
	 * @param hexKey 密钥（Hex格式）
	 * @return 加密后的Base64字符串（包含IV）
	 */
	public static String encryptCBC(String plaintext, String hexKey) {
		return encryptCBC(plaintext, hexKey, null);
	}

	/**
	 * SM4 CBC模式解密
	 * 
	 * @param ciphertext 密文（Base64格式，包含IV）
	 * @param hexKey 密钥（Hex格式）
	 * @return 解密后的明文
	 */
	public static String decryptCBC(String ciphertext, String hexKey) {
		try {
			byte[] key = hexToBytes(hexKey);
			byte[] data = Base64.decodeBase64(ciphertext);

			if (data.length < IV_LENGTH) {
				throw new EncryptionException("密文格式错误：数据长度不足");
			}

			// 提取IV和密文
			byte[] iv = new byte[IV_LENGTH];
			byte[] encrypted = new byte[data.length - IV_LENGTH];
			System.arraycopy(data, 0, iv, 0, IV_LENGTH);
			System.arraycopy(data, IV_LENGTH, encrypted, 0, encrypted.length);

			SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			byte[] decrypted = cipher.doFinal(encrypted);
			return new String(decrypted, StandardCharsets.UTF_8);
		}
		catch (Exception e) {
			throw new EncryptionException("SM4 CBC解密失败", e);
		}
	}

	/**
	 * SM4 CBC模式解密（字节数组版本）
	 * 
	 * @param encryptedData 密文数据（包含IV）
	 * @param hexKey 密钥（Hex格式）
	 * @param iv IV（16字节）
	 * @return 解密后的明文
	 */
	public static String decryptCBC(byte[] encryptedData, String hexKey, byte[] iv) {
		try {
			byte[] key = hexToBytes(hexKey);

			SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			byte[] decrypted = cipher.doFinal(encryptedData);
			return new String(decrypted, StandardCharsets.UTF_8);
		}
		catch (Exception e) {
			throw new EncryptionException("SM4 CBC解密失败", e);
		}
	}

	/**
	 * SM4 GCM模式加密（适用于长文本）
	 * 
	 * @param plaintext 明文
	 * @param hexKey 密钥（Hex格式，32个字符）
	 * @param iv IV（16字节），如果为null则自动生成
	 * @return 加密后的Base64字符串（包含IV）
	 */
	public static String encryptGCM(String plaintext, String hexKey, byte[] iv) {
		try {
			byte[] key = hexToBytes(hexKey);
			if (iv == null) {
				iv = generateIV();
			}

			SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			Cipher cipher = Cipher.getInstance("SM4/GCM/NoPadding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

			byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

			// 将IV和密文组合：IV(16字节) + 密文
			byte[] result = new byte[IV_LENGTH + encrypted.length];
			System.arraycopy(iv, 0, result, 0, IV_LENGTH);
			System.arraycopy(encrypted, 0, result, IV_LENGTH, encrypted.length);

			return Base64.encodeBase64String(result);
		}
		catch (Exception e) {
			throw new EncryptionException("SM4 GCM加密失败", e);
		}
	}

	/**
	 * SM4 GCM模式加密（自动生成IV）
	 * 
	 * @param plaintext 明文
	 * @param hexKey 密钥（Hex格式）
	 * @return 加密后的Base64字符串（包含IV）
	 */
	public static String encryptGCM(String plaintext, String hexKey) {
		return encryptGCM(plaintext, hexKey, null);
	}

	/**
	 * SM4 GCM模式解密
	 * 
	 * @param ciphertext 密文（Base64格式，包含IV）
	 * @param hexKey 密钥（Hex格式）
	 * @return 解密后的明文
	 */
	public static String decryptGCM(String ciphertext, String hexKey) {
		try {
			byte[] key = hexToBytes(hexKey);
			byte[] data = Base64.decodeBase64(ciphertext);

			if (data.length < IV_LENGTH) {
				throw new EncryptionException("密文格式错误：数据长度不足");
			}

			// 提取IV和密文
			byte[] iv = new byte[IV_LENGTH];
			byte[] encrypted = new byte[data.length - IV_LENGTH];
			System.arraycopy(data, 0, iv, 0, IV_LENGTH);
			System.arraycopy(data, IV_LENGTH, encrypted, 0, encrypted.length);

			SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			Cipher cipher = Cipher.getInstance("SM4/GCM/NoPadding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

			byte[] decrypted = cipher.doFinal(encrypted);
			return new String(decrypted, StandardCharsets.UTF_8);
		}
		catch (Exception e) {
			throw new EncryptionException("SM4 GCM解密失败", e);
		}
	}

	/**
	 * SM4 GCM模式解密（字节数组版本）
	 * 
	 * @param encryptedData 密文数据（不包含IV）
	 * @param hexKey 密钥（Hex格式）
	 * @param iv IV（16字节）
	 * @return 解密后的明文
	 */
	public static String decryptGCM(byte[] encryptedData, String hexKey, byte[] iv) {
		try {
			byte[] key = hexToBytes(hexKey);

			SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			Cipher cipher = Cipher.getInstance("SM4/GCM/NoPadding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

			byte[] decrypted = cipher.doFinal(encryptedData);
			return new String(decrypted, StandardCharsets.UTF_8);
		}
		catch (Exception e) {
			throw new EncryptionException("SM4 GCM解密失败", e);
		}
	}

	/**
	 * 生成SM4密钥（128位，32个十六进制字符）
	 * 
	 * @return Hex格式的密钥
	 */
	public static String generateKey() {
		byte[] key = new byte[KEY_SIZE / 8];
		random.nextBytes(key);
		return Hex.encodeHexString(key);
	}

	/**
	 * 根据文本长度自动选择加密模式
	 * 短文本（<= 32字符）使用CBC，长文本使用GCM
	 * 
	 * @param plaintext 明文
	 * @param hexKey 密钥（Hex格式）
	 * @return 加密后的Base64字符串
	 */
	public static String encryptAuto(String plaintext, String hexKey) {
		if (plaintext.length() <= 32) {
			return encryptCBC(plaintext, hexKey);
		}
		else {
			return encryptGCM(plaintext, hexKey);
		}
	}

	/**
	 * 自动解密（自动识别CBC或GCM模式）
	 * 注意：此方法会先尝试GCM，失败后尝试CBC
	 * 
	 * @param ciphertext 密文（Base64格式）
	 * @param hexKey 密钥（Hex格式）
	 * @return 解密后的明文
	 */
	public static String decryptAuto(String ciphertext, String hexKey) {
		// 尝试GCM模式（更安全，优先尝试）
		try {
			return decryptGCM(ciphertext, hexKey);
		}
		catch (Exception e) {
			// 如果GCM失败，尝试CBC模式
			return decryptCBC(ciphertext, hexKey);
		}
	}

	// ========== 工具方法 ==========

	/**
	 * 生成IV（16字节）
	 * 
	 * @return IV字节数组
	 */
	private static byte[] generateIV() {
		byte[] iv = new byte[IV_LENGTH];
		random.nextBytes(iv);
		return iv;
	}

	/**
	 * Hex字符串转字节数组
	 * 
	 * @param hex Hex字符串
	 * @return 字节数组
	 */
	private static byte[] hexToBytes(String hex) {
		try {
			return Hex.decodeHex(hex.toCharArray());
		}
		catch (Exception e) {
			throw new EncryptionException("Hex字符串格式错误: " + hex, e);
		}
	}

}
