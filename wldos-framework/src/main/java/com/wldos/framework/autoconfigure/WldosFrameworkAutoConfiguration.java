/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * WLDOS框架自动配置类
 * 为第三方项目提供WLDOS框架核心组件的自动配置
 * 
 * 注意：
 * - 此类的ComponentScan固定扫描com.wldos.framework包，用于加载框架自身的组件
 * - 第三方应用的包路径通过wldos.framework.base-package配置，由GlobalResponseHandler和GlobalExceptionHandler使用
 * - 第三方应用需要在自己的配置类中配置ComponentScan来扫描自己的包路径
 * 
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
@Configuration
@ConditionalOnClass(name = "com.wldos.framework.support.internal.MyJdbcRepository")
@EnableConfigurationProperties(WldosFrameworkProperties.class)
@ConditionalOnProperty(name = "wldos.framework.auto-config.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(
    basePackages = "com.wldos.framework",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.wldos\\.plugin\\..*"  // 排除插件包
    )
)
public class WldosFrameworkAutoConfiguration {
    
    /**
     * WLDOS框架自动配置标识
     * 用于标识当前项目使用了WLDOS框架自动配置
     */
    @Bean
    @ConditionalOnMissingBean
    public String wldosFrameworkAutoConfig() {
        return "wldos-framework-auto-configuration";
    }
    
    // 注意：这里可以添加更多WLDOS框架的核心Bean
    // 例如：MyJdbcRepository、WldosFrameworkService等
    // 但需要确保这些Bean在第三方项目中确实需要
}
