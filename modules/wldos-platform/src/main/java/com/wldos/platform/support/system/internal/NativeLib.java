/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

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
