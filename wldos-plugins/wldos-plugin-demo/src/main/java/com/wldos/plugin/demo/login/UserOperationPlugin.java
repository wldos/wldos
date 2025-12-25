/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.plugin.demo.login;

import com.wldos.framework.support.hook.HookType;
import com.wldos.framework.support.hook.WLDOSHook;
import com.wldos.framework.support.plugins.AbstractPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * demo插件主类
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/15
 */
@Slf4j
@Component
public class UserOperationPlugin extends AbstractPlugin {

    // 扩展点名称对应扩展点枚举的code
    @WLDOSHook(extName = "user.login", type = HookType.INVOKE, priority = 10, numArgs = 1)
    public void loginMessageInvoker(Object... args) {
        if (args != null && args[0] != null) {
            String username = args[0].toString();
            log.info("=== Plugin Event === User {} logged in successfully!", username);
        }
        log.info("=== Plugin Event === LoginMessageInvoker");
    }

    @WLDOSHook(extName = "article.view", type = HookType.HANDLER, priority = 10, numArgs = 1)
    public Object contentMaskHandler(Object... args) {
        if (args != null && args[0] != null) {
            String content = args[0].toString();

            // 简单的脱敏处理：替换手机号和邮箱

            log.info("=== Plugin Event === Article content has been masked for sensitive information");
            return content;
        }
        return args[0];
    }

    @Override
    public void start() throws Exception {
        super.start();
        // 自定义启动逻辑
    }
}
