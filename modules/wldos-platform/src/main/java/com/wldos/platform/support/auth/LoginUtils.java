/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.auth;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 登录验证算法和加密相关算法。
 *
 * @author 元悉宇宙
 * @date 2022/1/27
 * @version 1.0
 */
public interface LoginUtils extends PasswordEncoder {

	String encode(CharSequence var1);

	boolean matches(CharSequence var1, String var2);

	default boolean upgradeEncoding(String encodedPassword) {
		return false;
	}

	/**
	 * 16位16进制密码加密key生成策略，用于密码前后端传递时的加密
	 *
	 * @param username 用户登陆名
	 * @return 加密后的密码
	 */
	String enCryHexKey(String username, String hexKeyCode);

	/**
	 * 密码比对
	 *
	 * @param loginName 登录名
	 * @param passwdInput 输入的密码密文（前端加密）
	 * @param passwdEnc 数据库加密后的密码（不可逆）
	 * @param hexKeyCode 密码解密密钥
	 * @return boolean 是否匹配
	 */
	boolean verify(String loginName, String passwdInput, String passwdEnc, String hexKeyCode);

	boolean verifyRSA(String passwdInput, String passwdEnc);

	/**
	 * 验证密码并返回是否需要迁移（用于兼容旧密码格式）
	 * 
	 * @param plaintextPasswd 明文密码
	 * @param encryptedPassword 数据库中的加密密码
	 * @return 0-验证失败，1-新格式验证成功，2-旧格式验证成功（需要迁移）
	 */
	default int matchesWithMigrationCheck(CharSequence plaintextPasswd, String encryptedPassword) {
		// 默认实现：只调用 matches，不支持迁移检查
		return matches(plaintextPasswd, encryptedPassword) ? 1 : 0;
	}

	/**
	 * RSA密码验证并返回迁移检查结果（用于兼容旧密码格式）
	 * 
	 * @param passwdInput 前端RSA加密的密码
	 * @param passwdEnc 数据库中的加密密码
	 * @return PasswordVerifyResult 包含验证结果和明文密码（用于迁移）
	 */
	default PasswordVerifyResult verifyRSAWithMigrationCheck(String passwdInput, String passwdEnc) {
		// 默认实现：只调用 verifyRSA，不支持迁移检查
		boolean verified = verifyRSA(passwdInput, passwdEnc);
		return new PasswordVerifyResult(verified ? 1 : 0, null);
	}

	/**
	 * 密码验证结果（包含迁移信息）
	 */
	class PasswordVerifyResult {
		/** 验证结果：0-失败，1-新格式成功，2-旧格式成功（需迁移） */
		private final int result;
		/** 明文密码（用于迁移，仅在需要迁移时提供） */
		private final String plaintextPasswd;

		public PasswordVerifyResult(int result, String plaintextPasswd) {
			this.result = result;
			this.plaintextPasswd = plaintextPasswd;
		}

		public int getResult() {
			return result;
		}

		public String getPlaintextPasswd() {
			return plaintextPasswd;
		}

		public boolean isVerified() {
			return result > 0;
		}

		public boolean needsMigration() {
			return result == 2;
		}
	}
}