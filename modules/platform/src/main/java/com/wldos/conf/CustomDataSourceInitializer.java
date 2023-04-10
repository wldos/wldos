/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.conf;

import javax.sql.DataSource;

import com.wldos.sys.base.service.OptionsNoRepoService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * 。
 *
 * @author 树悉猿
 * @date 2023/4/9
 * @version 1.0
 */
@Configuration
public class CustomDataSourceInitializer {

	@Value("classpath:init.sql")
	private Resource sql;

	private final OptionsNoRepoService service;

	public CustomDataSourceInitializer(OptionsNoRepoService service) {
		this.service = service;
	}

	@Bean
	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
		final DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator());
		return initializer;
	}

	private DatabasePopulator databasePopulator() {
		final ResourceDatabasePopulator populate = new ResourceDatabasePopulator();

		try {
			this.service.getSystemOptions();
		}
		catch (DataAccessException ignored) {
			populate.setSqlScriptEncoding("UTF-8");
			populate.addScripts(sql);
		}
		catch (Exception ignored) {}

		return populate;
	}
}
