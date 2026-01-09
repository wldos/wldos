/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

/**
 * 应用来源（与可见范围 AppTypeEnum 解耦）
 * WLDOS采用"极简应用管理 + 强大插件生态"策略，应用市场就是插件市场
 */
public enum AppOriginEnum {
    INTERNAL("内部创建", "internal"),        // 内部创建
    PLUGIN("插件扩展", "plugin"), // 插件扩展（插件转系统应用）
    EXTERNAL("外部系统", "external");        // 外部系统对接（OpenAPI/端点）

    private final String label;
    private final String value;

    AppOriginEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{label: '" + this.label + "', value: '" + this.value + "'}";
    }
}


