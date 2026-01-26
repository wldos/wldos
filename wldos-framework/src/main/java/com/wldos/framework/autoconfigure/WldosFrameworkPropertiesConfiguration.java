/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WldosFrameworkProperties 配置类
 * 
 * 独立于 WldosFrameworkAutoConfiguration，确保 WldosFrameworkProperties 在所有环境下都会被注册。
 * 
 * 设计说明：
 * - WldosFrameworkAutoConfiguration 用于第三方项目的自动配置（可通过 wldos.framework.auto-config.enabled 控制）
 * - 本配置类无条件注册 WldosFrameworkProperties，供 GlobalResponseHandler 等组件使用
 * 
 * @author 元悉宇宙
 * @date 2026-01-25
 */
@Configuration
@EnableConfigurationProperties(WldosFrameworkProperties.class)
public class WldosFrameworkPropertiesConfiguration {
}
