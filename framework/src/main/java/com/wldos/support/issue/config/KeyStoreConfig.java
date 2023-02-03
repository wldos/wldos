/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.config;

import java.io.IOException;
import java.io.InputStream;

import de.schlichtherle.license.AbstractKeyStoreParam;

import org.springframework.core.io.ClassPathResource;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public class KeyStoreConfig extends AbstractKeyStoreParam {

	private final String storePath;

	private final String alias;

	private final String storePwd;

	private final String keyPwd;

	public KeyStoreConfig(Class clazz, String resource, String alias, String storePwd, String keyPwd) {
		super(clazz, resource);
		this.storePath = resource;
		this.alias = alias;
		this.storePwd = storePwd;
		this.keyPwd = keyPwd;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public String getStorePwd() {
		return storePwd;
	}

	@Override
	public String getKeyPwd() {
		return keyPwd;
	}

	@Override
	public InputStream getStream() throws IOException {
		ClassPathResource resource = new ClassPathResource(storePath);
		return resource.getInputStream();
	}
}