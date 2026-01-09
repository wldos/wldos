/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.Serializable;
import java.net.URLClassLoader;

/**
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/22
 */
public interface IPlugin extends Serializable {
    void setClassLoader(URLClassLoader urlClassLoader, ConfigurableEnvironment env);
    void register(ConfigurableApplicationContext context);
    void boot(ConfigurableApplicationContext context);
}
