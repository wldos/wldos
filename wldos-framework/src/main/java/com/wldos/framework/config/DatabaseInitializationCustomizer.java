/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * 数据库初始化自定接口
 * 依赖wldos框架后实现该接口，框架自动加载自定义的初始化配置
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/22
 */
public interface DatabaseInitializationCustomizer {
    void customize(ResourceDatabasePopulator populator);
}