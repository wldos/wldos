/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.config;

import com.wldos.framework.config.DatabaseInitializationCustomizer;

import com.wldos.platform.core.service.OptionsNoRepoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

/**
 * 自定义数据源初始化器。
 *
 * @author 元悉宇宙
 * @date 2023/4/9
 * @version 1.0
 */
@Component
public class PlatformDataSourceInitializer implements DatabaseInitializationCustomizer {

	@Value("classpath:db/init.sql")
	private Resource sql;

	private final OptionsNoRepoService service;

	public PlatformDataSourceInitializer(OptionsNoRepoService service) {
		this.service = service;
	}

	@Override
	public void customize(ResourceDatabasePopulator populator) {
		try {
			this.service.getSystemOptions();
		}
		catch (DataAccessException ignored) {
			populator.addScripts(sql);
		}
		catch (Exception ignored) {}
	}
}
