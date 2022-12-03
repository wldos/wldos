/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.wldos.support.issue.utils.ByteCodeSign;

import org.springframework.core.io.ClassPathResource;

/**
 * wldos底层安全模式加载。
 *
 * @author 树悉猿
 * @date 2022/2/5
 * @version 1.0
 */
public final class WLDOSUtilities extends ClassLoader {
	// 覆写springboot或者其他框架classLoader
	private static final ClassLoader parent = Thread.currentThread().getContextClassLoader();

	private static WLDOSUtilities wldosUtilities;

	public static ClassLoader getInstance() {
		if (wldosUtilities == null)
			wldosUtilities = new WLDOSUtilities(parent);
		return wldosUtilities;
	}

	public WLDOSUtilities(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String name) {
		return load(name);
	}

	private Class<?> load(String className) {
		Class<?> newClass = null;
		InputStream byteCodeIn = null;
		InputStream loaderIn = null;
		ByteArrayOutputStream out = null;
		try {
			boolean isVerifier = "wldos_vri".equals(className);
			className =  isVerifier ? "VerifierImpl" : className;
			newClass = findLoadedClass(className);
			if (newClass != null)
				return newClass;

			ClassPathResource loader = new ClassPathResource("/com/wldos/support/issue/WLDOSUtilities.class");
			loaderIn = loader.getInputStream();
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = loaderIn.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
			byte[] loaderByte = out.toByteArray();
			out.close();
			loaderIn.close();
			String loaderSign = ByteCodeSign.md5(loaderByte);

			if (isVerifier) {
				ClassPathResource resource = new ClassPathResource(File.separator + "issue.property");
				byteCodeIn = resource.getInputStream();
				out = new ByteArrayOutputStream();
				while ((len = byteCodeIn.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.flush();
				byte[] byteCodeData = out.toByteArray();
				out.close();
				byteCodeIn.close();
				if (byteCodeData.length > 0) {
					SecretKeySpec sks = new SecretKeySpec(loaderSign.getBytes(StandardCharsets.UTF_8), "AES");
					Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, sks);
					byte[] verifierByteCode = cipher.doFinal(byteCodeData);
					newClass = defineClass(className, verifierByteCode, 0, verifierByteCode.length);
				}
			}
			else {
				String encryptText = className.startsWith("F") ? "issue.key" : "issue.store";
				ClassPathResource resource = new ClassPathResource(File.separator + encryptText);
				byteCodeIn = resource.getInputStream();
				out = new ByteArrayOutputStream();
				while ((len = byteCodeIn.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.flush();
				byte[] byteCodeData = out.toByteArray();
				out.close();
				byteCodeIn.close();
				if (byteCodeData.length > 0) {
					SecretKeySpec sks = new SecretKeySpec(loaderSign.getBytes(StandardCharsets.UTF_8), "AES");
					Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, sks);
					byte[] verifierByteCode = cipher.doFinal(byteCodeData);
					newClass = defineClass(className, verifierByteCode, 0, verifierByteCode.length);
				}
			}
			if (newClass == null)
				newClass = findSystemClass(className);
			if (newClass != null)
				resolveClass(newClass);
		}
		catch (Exception e) {
			System.out.println("加载证书文件失败，请不要更改系统文件");
		}
		finally {
			try {
				if (byteCodeIn != null)
					byteCodeIn.close();
				if (out != null)
					out.close();
				if (loaderIn != null)
					loaderIn.close();
			}
			catch (IOException ignored) {
			}
		}
		return newClass;
	}

	public WLDOSUtilities() {}
}