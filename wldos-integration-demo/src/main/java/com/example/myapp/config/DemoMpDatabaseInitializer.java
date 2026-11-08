/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.config;

import com.wldos.framework.config.DatabaseInitializationCustomizer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

/**
 * 集成 demo 追加 H2 表与种子数据，供 MyBatis-Plus 金标准接口使用。
 */
@Component
public class DemoMpDatabaseInitializer implements DatabaseInitializationCustomizer {

	@Override
	public void customize(ResourceDatabasePopulator populator) {
		populator.addScript(new ClassPathResource("db/demo-article-h2.sql"));
	}
}
