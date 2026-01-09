/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.impl;

import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.framework.support.web.EdgeHandler;
import com.wldos.framework.support.web.FilterRequestWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/25
 */
@ConditionalOnMissingClass("com.wldos.support.web.impl.EdgeHandlerImpl")
@Component
public class DefaultEdgeHandlerImpl implements EdgeHandler {
    @Override
    public Object handleBody(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return null;
    }

    @Override
    public void handleResponse(HttpServletResponse response) {

    }

    @Override
    public FilterRequestWrapper handleRequest(HttpServletRequest request, JWT jwt, Long domainId, String proxyPrefix) {
        return null;
    }

    @Override
    public String getDomain(HttpServletRequest request, String domainHeader) {
        return null;
    }
}
