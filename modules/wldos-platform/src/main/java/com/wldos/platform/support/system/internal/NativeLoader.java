/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

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
