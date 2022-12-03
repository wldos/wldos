/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 登录验证算法和加密相关算法。
 *
 * @author 树悉猿
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
}