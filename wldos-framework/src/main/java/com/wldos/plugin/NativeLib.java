/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.plugin;

import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

public class NativeLib extends JarEntry {
	public NativeLib(String name) {
		super(name);
	}

	public NativeLib(ZipEntry ze) {
		super(ze);
	}

	public NativeLib(JarEntry je) {
		super(je);
	}

	public String getLibName(ClassLoader loader) {
		LoadNativeLib.loadLibraryI(loader);
		return this.getName();
	}
}
