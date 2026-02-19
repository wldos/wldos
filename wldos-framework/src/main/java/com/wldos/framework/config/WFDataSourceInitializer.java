/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * 框架默认的数据库初始化（允许依赖模块自定义追加初始化脚本）
 * H2 使用 framework-init-h2.sql，MySQL 使用 framework-init.sql
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/22
 */
@Configuration
public class WFDataSourceInitializer {

    @Value("classpath:db/framework-init.sql")
    private Resource frameworkSql;

    @Value("classpath:db/framework-init-h2.sql")
    private Resource frameworkSqlH2;

    @SuppressWarnings("all")
    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource,
                                                       ObjectProvider<DatabaseInitializationCustomizer> customizers) {

        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(createDatabasePopulator(dataSource, customizers));
        return initializer;
    }

    private DatabasePopulator createDatabasePopulator(DataSource dataSource,
            ObjectProvider<DatabaseInitializationCustomizer> customizers) {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("UTF-8");

        try (java.sql.Connection conn = dataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            if (url != null && url.startsWith("jdbc:h2:")) {
                populator.addScripts(frameworkSqlH2);
            } else {
                populator.addScripts(frameworkSql);
            }
        } catch (Exception e) {
            populator.addScripts(frameworkSql);
        }

        // 允许二开模块追加自定义初始化行为
        customizers.orderedStream().forEach(customizer ->
                customizer.customize(populator));

        return populator;
    }
}