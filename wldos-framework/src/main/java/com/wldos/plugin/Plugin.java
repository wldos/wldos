/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.plugin;

import java.util.Arrays;
import java.util.List;

public class Plugin {

	private Integer id;

	private String name;

	private String url;

	private String bootClass;

	private String scanPath;

	private String config;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBootClass() {
		return bootClass;
	}

	public void setBootClass(String bootClass) {
		this.bootClass = bootClass;
	}

	public List<String> getScanPath() {
		return Arrays.asList(scanPath.split(","));
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}