/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密和密文比对。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
public class PasswdEncode implements PasswordEncoder {
	public static PasswdEncode getInstance() {
		return new PasswdEncode();
	}

	@SneakyThrows
	@Override
	public String encode(CharSequence plaintextPasswd) {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] digest = messageDigest.digest(plaintextPasswd.toString().getBytes(StandardCharsets.UTF_8));
		return new String(Base64.encodeBase64(digest));
	}

	@Override
	public boolean matches(CharSequence plaintextPasswd, String encryptedPassword) {

		return this.encode(plaintextPasswd.toString()).equals(encryptedPassword);
	}

	@Override
	public boolean upgradeEncoding(String encodedPassword) {
		return false;
	}

	public static void main(String[] args) {
		PasswordEncoder pe = PasswdEncode.getInstance();
		String pse = pe.encode("123456");
		String abc = "iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=";

		System.out.println("pse=" + pse + " " + pe.matches("wldos", abc));
	}
}
