/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.plugin;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * load native file。
 *
 * @author 树悉猿
 * @date 2023/6/24
 * @version 1.0
 */
public class LoadNativeLib extends JarFile implements NativeLoader {
	public LoadNativeLib(String name) throws IOException {
		super(name);
	}

	public LoadNativeLib(String name, boolean verify) throws IOException {
		super(name, verify);
	}

	public LoadNativeLib(File file) throws IOException {
		super(file);
	}

	public LoadNativeLib(File file, boolean verify) throws IOException {
		super(file, verify);
	}

	public LoadNativeLib(File file, boolean verify, int mode) throws IOException {
		super(file, verify, mode);
	}

	public static native void loadLibrary(Class<?> clazz, String headerFile);

	public static native void loadLibrary(ClassLoader loader);

	public static void loadLibraryI(ClassLoader loader) {
	}
}
