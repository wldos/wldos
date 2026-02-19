/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.config;

import com.wldos.framework.support.storage.interceptor.WebPImageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebP 图片转换拦截器配置
 * 
 * @author 元悉宇宙
 * @date 2026-01-27
 */
@Configuration
public class WebPImageConfig implements WebMvcConfigurer {
    
    @Autowired
    private WebPImageInterceptor webPImageInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webPImageInterceptor)
                .addPathPatterns("/store/**")
                .order(1); // 在静态资源处理之前
    }
}
