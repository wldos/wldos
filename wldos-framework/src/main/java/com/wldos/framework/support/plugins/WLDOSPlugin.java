/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.plugins;

import com.wldos.framework.support.plugins.core.IPluginMetadata;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * wldos插件
 *
 * @author 元悉宇宙
 * @date 2025/2/15
 * @version 1.0
 */
public interface WLDOSPlugin {
    // 插件元数据
    IPluginMetadata getMetadata();
    
    // 生命周期管理
    void init() throws Exception;
    void start() throws Exception;
    void stop() throws Exception;
    void destroy() throws Exception;
    void uninstall();
    
    // Spring集成
    void setApplicationContext(ApplicationContext context);

    /**
     * 导出插件状态
     */
    default Map<String, Object> exportState() {
        return new HashMap<>();
    }

    /**
     * 导入插件状态
     */
    default void importState(Map<String, Object> state) {
    }
}
