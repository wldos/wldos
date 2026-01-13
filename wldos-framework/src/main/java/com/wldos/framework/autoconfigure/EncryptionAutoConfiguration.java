/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 加密算法自动配置类
 * 独立配置EncryptionProperties，不依赖于WldosFrameworkAutoConfiguration
 * 
 * @author 元悉宇宙
 * @date 2026/01/12
 */
@Configuration
@EnableConfigurationProperties(EncryptionProperties.class)
@ConditionalOnProperty(name = "wldos.encryption.enabled", havingValue = "true", matchIfMissing = true)
public class EncryptionAutoConfiguration {
    // 独立配置EncryptionProperties，无需额外代码
}