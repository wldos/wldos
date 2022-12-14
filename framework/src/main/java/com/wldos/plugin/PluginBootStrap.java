/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.plugin;

import org.springframework.context.ConfigurableApplicationContext;

public interface PluginBootStrap {
    void boot(ConfigurableApplicationContext context);
}