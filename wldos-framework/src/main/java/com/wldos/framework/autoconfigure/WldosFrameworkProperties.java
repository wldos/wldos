/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
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
 * @author WLDOS Team
 */
@ConfigurationProperties(prefix = "wldos.framework")
public class WldosFrameworkProperties {
    
    /**
     * 是否启用WLDOS框架自动配置
     */
    private boolean enabled = true;
    
    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
