/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.plugin;

import com.wldos.platform.support.plugin.WLDOSPlugin;
import com.wldos.platform.support.plugin.core.IPluginMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
public abstract class AbstractPlugin implements WLDOSPlugin {
    protected IPluginMetadata metadata;
    protected ApplicationContext applicationContext;
    protected boolean running = false;
    
    @Override
    public IPluginMetadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(IPluginMetadata metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public void init() throws Exception {
        log.info("Initializing plugin: {}", metadata.getName());
    }
    
    @Override
    public void start() throws Exception {
        log.info("Starting plugin: {}", metadata.getName());
        running = true;
    }
    
    @Override
    public void stop() throws Exception {
        log.info("Stopping plugin: {}", metadata.getName());
        running = false;
    }
    
    @Override
    public void destroy() throws Exception {
        log.info("Destroying plugin: {}", metadata.getName());
    }
    
    @Override
    public void uninstall() {
        log.info("Uninstalling plugin: {}", metadata.getName());
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
} 