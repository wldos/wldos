/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.wldos.framework.support.internal.MyJdbcRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.MyBatisJdbcConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
/**
 * 配置mybatis-plus。
 *
 * @author Yuanxi Universe
 * @date 2023/10/21
 * @version 1.0
 */
@Configuration
@EnableJdbcRepositories(
    basePackages= {
			"${spring.data.jdbc.packages:com.wldos.**.dao}",
			"${spring.data.jdbc.packages.ext:${wldos.framework.base-package:com.wldos.never}.**.dao}"
	},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.wldos\\.plugin\\..*"
    ),
    repositoryBaseClass = MyJdbcRepository.class
)
@Import(MyBatisJdbcConfiguration.class)
public class MyBatisPlusConfiguration {

	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor()); // 乐观锁
		interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor()); // 防止全表更新或删除
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
		return interceptor;
	}

	@Bean
	@ConditionalOnProperty(name = "database", havingValue = "oracle")
	public OracleKeyGenerator oracleKeyGenerator() {
		return new OracleKeyGenerator();
	}
}
