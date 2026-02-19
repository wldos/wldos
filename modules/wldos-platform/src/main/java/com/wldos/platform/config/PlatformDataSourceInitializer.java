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

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * 自定义数据源初始化器。
 * H2 使用 init-h2.sql，MySQL 使用 init.sql
 * 当 wo_options 或 wo_plugin_registry 等关键表不存在时执行初始化脚本。
 *
 * @author 元悉宇宙
 * @date 2023/4/9
 * @version 1.0
 */
@Component
public class PlatformDataSourceInitializer implements DatabaseInitializationCustomizer {

	@Value("classpath:db/init.sql")
	private Resource sql;

	@Value("classpath:db/init-h2.sql")
	private Resource sqlH2;

	private final OptionsNoRepoService service;
	private final DataSource dataSource;

	public PlatformDataSourceInitializer(OptionsNoRepoService service, DataSource dataSource) {
		this.service = service;
		this.dataSource = dataSource;
	}

	@Override
	public void customize(ResourceDatabasePopulator populator) {
		if (needsInit()) {
			Resource script = isH2() ? sqlH2 : sql;
			populator.addScripts(script);
		}
	}

	/**
	 * 判断是否需要执行初始化脚本。
	 * 1. getSystemOptions 失败（wo_options 不存在）时需初始化
	 * 2. H2 下 wo_plugin_registry 不存在时也需初始化（修复部分初始化导致的缺失表）
	 */
	private boolean needsInit() {
		try {
			this.service.getSystemOptions();
			// getSystemOptions 成功，再检查 wo_plugin_registry 是否存在（H2 部分初始化场景）
			if (isH2() && !tableExists("wo_plugin_registry")) {
				return true;
			}
			return false;
		}
		catch (DataAccessException ignored) {
			return true;
		}
		catch (Exception ignored) {
			return true;
		}
	}

	private boolean tableExists(String tableName) {
		try (java.sql.Connection conn = dataSource.getConnection()) {
			// H2 表名可能为大写，尝试多种写法
			String[] variants = {tableName, tableName.toUpperCase(), tableName.toLowerCase()};
			for (String name : variants) {
				try (ResultSet rs = conn.getMetaData().getTables(null, null, name, new String[]{"TABLE"})) {
					if (rs.next()) return true;
				}
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private boolean isH2() {
		try (java.sql.Connection conn = dataSource.getConnection()) {
			String url = conn.getMetaData().getURL();
			return url != null && url.startsWith("jdbc:h2:");
		}
		catch (Exception e) {
			return false;
		}
	}
}
