/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WLDOS框架配置属性
 * 
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
@ConfigurationProperties(prefix = "wldos.framework")
public class WldosFrameworkProperties {
    
    /**
     * 是否启用WLDOS框架自动配置
     */
    private boolean enabled = true;
    
    /**
     * 基础包路径（用于组件扫描和异常处理）
     * 第三方应用可以配置自己的包路径，例如：com.example.myapp
     * 如果不配置，默认使用 com.wldos
     */
    private String basePackage = "com.wldos";
    
    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getBasePackage() {
        return basePackage;
    }
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
