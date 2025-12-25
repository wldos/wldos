/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记不需要进行全局响应包装的方法或类
 * 
 * 使用场景：
 * 1. 文件下载接口
 * 2. 返回原始数据的接口
 * 3. 需要自定义响应格式的接口
 * 
 * @author 元悉宇宙
 * @date 2025/9/6
 * @version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotResponseBody {
    
    /**
     * 不进行响应包装的原因说明
     */
    String value() default "";
}
