/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.wldos.framework.support.internal.MyJdbcRepository;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.MyBatisJdbcConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.util.StringUtils;

import java.util.Arrays;

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
@MapperScan(basePackages = {
		"${wldos.framework.base-package:com.wldos.never}",
		"com.wldos"
}, annotationClass = Mapper.class) // 支持mp技术栈通用mapper
@Import(MyBatisJdbcConfiguration.class)
public class MyBatisPlusConfiguration {

	/**
	 * 与 {@code wldos.framework.base-package} 联动，追加包路径下 {@code mapping} 目录中的 Mapper XML。
	 * base-package 为点分包名，classpath 须斜杠，故不能直接在 mapper-locations 写占位符。
	 */
	@Bean
	public MybatisPlusPropertiesCustomizer basePackageMappingXmlLocations(
			@Value("${wldos.framework.base-package:}") String basePackage) {
		return properties -> {
			if (!StringUtils.hasText(basePackage) || basePackage.contains(".never")) {
				return;
			}
			String classpathPrefix = "classpath*:" + basePackage.replace('.', '/') + "/**/mapping/*.xml";
			String[] current = properties.getMapperLocations();
			if (current == null || current.length == 0) {
				properties.setMapperLocations(new String[] { classpathPrefix });
				return;
			}
			for (String loc : current) {
				if (classpathPrefix.equals(loc)) {
					return;
				}
			}
			String[] merged = Arrays.copyOf(current, current.length + 1);
			merged[current.length] = classpathPrefix;
			properties.setMapperLocations(merged);
		};
	}

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
