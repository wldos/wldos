/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.plugins.core;

/**
 * 插件状态枚举类。
 *
 * @author 元悉宇宙
 * @date 2025/2/15
 * @version 1.0
 */
public enum PluginStatus {
    INSTALLED("已安装"),
    ENABLED("已启用"),
    DISABLED("已禁用"),
    UNINSTALLED("未安装"),
    ROLLBACK("回滚"),
    ERROR("错误");

    private final String description;

    PluginStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
