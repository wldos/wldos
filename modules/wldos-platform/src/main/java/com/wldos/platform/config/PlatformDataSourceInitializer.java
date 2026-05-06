/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.config;

import com.wldos.framework.config.DatabaseInitializationCustomizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * 自定义数据源初始化器。
 * H2 使用 init-h2.sql，MySQL 使用 init.sql
 * 当 platform 核心库未就绪（见 {@link #needsInit()}）时执行初始化脚本。
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

	private final DataSource dataSource;

	public PlatformDataSourceInitializer(DataSource dataSource) {
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
	 * 判断是否需要执行 platform 全量 init.sql。
	 * 框架层会先执行 {@code framework-init.sql}（含 wo_options 空表），若仅用
	 * {@code getSystemOptions()} 判断是否已初始化，空表也会“成功”从而跳过灌库。
	 * 因此以 {@code wo_org} 等 platform 独有表是否存在为准；H2 另检 {@code wo_plugin_registry}。
	 */
	private boolean needsInit() {
		try {
			if (!tableExists("wo_options")) {
				return true;
			}
			if (!tableExists("wo_org")) {
				return true;
			}
			if (isH2() && !tableExists("wo_plugin_registry")) {
				return true;
			}
			return false;
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
