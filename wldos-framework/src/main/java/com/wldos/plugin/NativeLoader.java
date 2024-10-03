/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 */

package com.wldos.plugin;

import java.util.Enumeration;
import java.util.jar.JarEntry;

/**
 * native load lib interface。
 *
 * @author 元悉宇宙
 * @date 2023/6/24
 * @version 1.0
 */
public interface NativeLoader {
	Enumeration<JarEntry> entries();
	static void loadLibraryI(Class<?> clazz, String headerFile, StringBuilder order) {}
	static void loadLibraryI(ClassLoader loader) {}
}
