/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
    
    /**
     * 开源版 FreeJdbcTemplate 实现
     * 仅在以下条件满足时注册：
     * 1. platform 模块不存在（通过 @ConditionalOnMissingClass 检测 IssueAuth，platform 模块的核心类）
     * 2. 未找到加密版实现（通过 @ConditionalOnMissingBean 确保）
     * 
     * 注意：当 platform 模块存在时，加密 JAR 会通过 VerifierImpl.install() 运行时加载并注册 CommonOperation，
     * 此时开源版实现会自动失效（因为 @ConditionalOnMissingClass 条件不满足）
     */
    @Bean
    @ConditionalOnMissingClass("com.wldos.support.issue.verify.IssueAuth")
    @ConditionalOnMissingBean(name = "commonOperation")
    public com.wldos.framework.common.FreeJdbcTemplate freeJdbcTemplate(
            com.wldos.framework.support.internal.BaseWrap baseWrap) {
        return new com.wldos.framework.common.impl.OpenSourceFreeJdbcTemplate(baseWrap);
    }
    
    /**
     * 开源版 CommonOperation 实现
     * 仅在以下条件满足时注册：
     * 1. platform 模块不存在（通过 @ConditionalOnMissingClass 检测 IssueAuth）
     * 2. 未找到加密版实现（通过 @ConditionalOnMissingBean 确保）
     * 
     * 注意：
     * - 如果存在 platform 模块（IssueAuth 类存在），加密版会通过 VerifierImpl.install() 运行时加载并注册
     * - 开源版只实现 JDBC API 操作（查询、分页等），其他业务相关 API（saveOrUpdate、dynamicInsertByEntity 等）为空实现
     * - 业务相关的 API（如 isAdmin、listSuperAdmin 等）返回默认值，不执行实际业务逻辑
     */
    @Bean(name = "commonOperation")
    @ConditionalOnMissingClass("com.wldos.support.issue.verify.IssueAuth")
    @ConditionalOnMissingBean(name = "commonOperation")
    public com.wldos.framework.common.CommonOperation commonOperation(
            com.wldos.framework.common.FreeJdbcTemplate freeJdbcTemplate) {
        return new com.wldos.framework.common.impl.OpenSourceCommonOperation(
                (com.wldos.framework.support.internal.God) freeJdbcTemplate);
    }
}
