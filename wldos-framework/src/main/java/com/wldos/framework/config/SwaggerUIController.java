/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Swagger UI 控制器
 * 重定向到 Knife4j 文档页面（默认就是左右分栏布局）
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 * @version 2.0
 */
@Controller
@ConditionalOnProperty(name = "wldos.framework.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerUIController {
    
    /**
     * 重定向到 Knife4j 文档页面
     * Knife4j 默认就是左右分栏布局（左侧 API 列表，右侧 API 详情和调试）
     * 访问路径：/doc.html
     */
    @GetMapping("/swagger-ui")
    public String swaggerUI() {
        return "redirect:/doc.html";
    }
}

