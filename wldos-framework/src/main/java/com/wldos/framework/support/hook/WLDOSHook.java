/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个hook
 * 使用在类上时，必须实现Handler或Invoker
 * 使用在方法上时，必须有可变参数，Handler必须返回处理结果，Invoker无须返回
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/7/18
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WLDOSHook {
    String extName();
    HookType type(); // 直接声明类型
    int priority() default 0;
    int numArgs() default 1;
    // 其他参数
}
