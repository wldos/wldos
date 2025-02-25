/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.plugin.demo;

import com.wldos.framework.support.plugins.Handler;
import com.wldos.framework.support.plugins.Invoker;
import com.wldos.framework.support.hook.WSHook;
import com.wldos.platform.support.plugin.AbstractPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * demo插件
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/15
 */
@Slf4j
@Component
public class UserOperationPlugin extends AbstractPlugin {

    @Override
    public void registerHooks(WSHook hookManager) {
        hookManager.addInvoke("user.login", LoginMessageInvoker.class, 10, 1);
        hookManager.addHandler("article.view", ContentMaskHandler.class, 20, 1);
    }

    @Override
    public void start() throws Exception {
        super.start();
        // 自定义启动逻辑
    }
}

@Slf4j
@Component
class LoginMessageInvoker implements Invoker {
    @Override
    public void invoke(Object... args) {
        if (args != null && args[0] != null) {
            String username = args[0].toString();
            log.info("=== Plugin Event === User {} logged in successfully!", username);
        }
    }
}

@Slf4j
@Component
class ContentMaskHandler implements Handler {
    @Override
    public Object applyHandler(Object... args) {
        if (args != null && args[0] != null) {
            String content = args[0].toString();

            // 简单的脱敏处理：替换手机号和邮箱
            String masked = content.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")
                    .replaceAll("(\\w{3})\\w+(@\\w+\\.\\w+)", "$1***$2");

            log.info("=== Plugin Event === Article content has been masked for sensitive information");
            return masked;
        }
        return args[0];
    }
}
