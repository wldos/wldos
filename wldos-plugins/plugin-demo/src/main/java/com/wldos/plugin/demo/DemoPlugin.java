/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.plugin.demo;

import com.wldos.framework.support.plugins.AbstractPlugin;
import com.wldos.framework.support.hook.WLDOSHook;
import com.wldos.framework.support.hook.HookType;
import com.wldos.framework.support.plugins.Handler;
import com.wldos.framework.support.plugins.Invoker;
import com.wldos.framework.support.plugins.core.IPluginMetadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * 插件开发演示插件
 * 作为插件开发者的基础框架和参考示例
 *
 * @author WLDOS Team
 * @date 2025/12/12
 * @version 1.0.0
 */
@Slf4j
@WLDOSHook(extName = "demo.plugin.init", type = HookType.INVOKE, priority = 10, numArgs = 1)
public class DemoPlugin extends AbstractPlugin implements Handler, Invoker {
    
    private final String pluginCode = "plugin-demo";
    
    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public void init() throws Exception {
        super.init();
        log.info("插件演示插件初始化完成");
    }
    
    @Override
    public void start() throws Exception {
        try {
            super.start();
            log.info("插件演示插件启动成功");
        } catch (Exception e) {
            log.error("插件演示插件启动失败", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        log.info("插件演示插件已停止");
    }
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
        log.info("插件演示插件已销毁");
    }
    
    @Override
    public IPluginMetadata getMetadata() {
        return this.metadata;
    }
    
    @Override
    public void uninstall() {
        super.uninstall();
        log.info("插件演示插件已卸载");
    }

    /**
     * 插件初始化Hook处理器
     */
    @WLDOSHook(extName = "demo.plugin.init", type = HookType.INVOKE, priority = 10, numArgs = 1)
    public void initPlugin(Object... args) {
        log.info("插件演示插件初始化Hook被触发");
    }
    
    /**
     * 插件清理Hook处理器
     */
    @WLDOSHook(extName = "demo.plugin.cleanup", type = HookType.INVOKE, priority = 10, numArgs = 1)
    public void cleanupPlugin(Object... args) {
        log.info("插件演示插件清理Hook被触发");
    }
    
    /**
     * 处理示例数据Hook处理器
     */
    @WLDOSHook(extName = "demo.example.process", type = HookType.HANDLER, priority = 10, numArgs = 1)
    public Object processExample(Object... args) {
        log.info("插件演示插件处理示例数据Hook被触发");
        if (args.length > 0) {
            log.info("接收到的数据: {}", args[0]);
            return "处理成功: " + args[0];
        }
        return "处理成功";
    }
    
    /**
     * Handler接口实现
     */
    @Override
    public Object applyHandler(Object... args) {
        if (args.length > 0) {
            String hookName = args[0].toString();
            log.info("插件演示插件处理Hook: {}", hookName);
            
            switch (hookName) {
                case "demo.example.process":
                    return processExample(args);
                default:
                    return "unknown_hook";
            }
        }
        return null;
    }
    
    /**
     * Invoker接口实现
     */
    @Override
    public void invoke(Object... args) {
        if (args.length > 0) {
            String hookName = args[0].toString();
            log.info("插件演示插件调用Hook: {}", hookName);
            
            switch (hookName) {
                case "demo.plugin.init":
                    initPlugin(args);
                    break;
                case "demo.plugin.cleanup":
                    cleanupPlugin(args);
                    break;
                default:
                    log.warn("未知的调用Hook: {}", hookName);
            }
        }
    }
}

