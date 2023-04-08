/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;

/**
 * 资源加载。
 *
 * @author 树悉猿
 * @date 2023/4/8
 * @version 1.0
 */
public class ResourceLoader {
	public static String appHome() {
		ApplicationHome home = new ApplicationHome();
		return home.getDir().getAbsolutePath() + File.separator;
	}

	public static InputStream loadFileResource(String filename)  {
		String appHome = appHome();
		System.out.println(appHome);
		try {
			return new FileInputStream(appHome + filename);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static InputStream loadClassPathResource(String resourceName)  {
		try {
			return new ClassPathResource(resourceName).getInputStream();
		}
		catch (IOException ignored) {
			return null;
		}
	}

	public static InputStream loadResource(String resourceName) {
		InputStream in = loadFileResource(resourceName);
		return in == null ? loadClassPathResource(resourceName) : in;
	}
}
