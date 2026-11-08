/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth;

import io.github.wldos.framework.support.auth.JwtSigningKeySupport;

/**
 * 生成或校验 {@code app.secret_key}（JJWT HS256 / RFC 7518，UTF-8 ≥ 32 字节）。
 * <p>
 * 在 IDE 中直接运行本类 {@link #main(String[])}，或在模块目录执行：
 * </p>
 * <pre>{@code
 * mvn -q -pl wldos-framework exec:java \
 *   -Dexec.mainClass=com.wldos.framework.support.auth.GenerateAppSecretKey
 * }</pre>
 * <p>带参数时仅校验，不生成：</p>
 * <pre>{@code
 * ... -Dexec.args="你的密钥明文"
 * }</pre>
 *
 * @author Yuanxi Universe
 * @date 2026-05-22
 * @version 1.0
 */
public final class GenerateAppSecretKey {

	private GenerateAppSecretKey() {
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
			validate(args[0].trim());
			return;
		}
		generate();
	}

	private static void generate() {
		String secret = JwtSigningKeySupport.generateAppSecretKey();
		int bytes = JwtSigningKeySupport.utf8ByteLength(secret);
		System.out.println("=== WLDOS app.secret_key 生成（HS256） ===");
		System.out.println();
		System.out.println("明文（" + bytes + " UTF-8 字节）：");
		System.out.println(secret);
		System.out.println();
		System.out.println("写入 application.properties / 环境变量：");
		System.out.println("app.secret_key=" + secret);
		System.out.println();
		System.out.println("说明：轮换密钥后用户需重新登录；勿提交到公开仓库。");
	}

	private static void validate(String secret) {
		int bytes = JwtSigningKeySupport.utf8ByteLength(secret);
		System.out.println("=== WLDOS app.secret_key 校验（HS256） ===");
		System.out.println("UTF-8 字节数: " + bytes + "（要求 >= " + JwtSigningKeySupport.HS256_MIN_KEY_BYTES + "）");
		try {
			JwtSigningKeySupport.requireValidAppSecretKey(secret);
			System.out.println("结果: 合法，可用于 app.secret_key");
		}
		catch (IllegalArgumentException e) {
			System.out.println("结果: 不合法 — " + e.getMessage());
			System.exit(1);
		}
	}
}
