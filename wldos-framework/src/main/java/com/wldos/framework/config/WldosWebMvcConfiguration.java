/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import com.wldos.framework.support.web.resolver.PageQueryMethodArgumentResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WLDOS Web MVC 配置（Swagger 禁用时生效）
 * <p>
 * 生产环境通常关闭 Swagger，此时 SwaggerConfig 不加载。
 * 本配置在 swagger.enabled=false 时注册 PageQuery 参数解析器，
 * 确保 Controller 可直接声明 PageQuery 参数。
 *
 * @author 元悉宇宙
 * @date 2026-02-01
 * @version 1.0
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "wldos.framework.swagger.enabled", havingValue = "false")
public class WldosWebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new PageQueryMethodArgumentResolver());
	}
}
