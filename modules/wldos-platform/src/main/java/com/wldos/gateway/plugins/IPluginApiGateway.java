/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.gateway.plugins;

import com.wldos.framework.support.auth.vo.JWT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/11/14
 */
public interface IPluginApiGateway {
    Set<String> getInstalledPluginCodes();

    /**
     * 转发插件API请求
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param pluginCode 插件代码
     * @param jwt JWT令牌
     * @param domainId 域名ID
     * @return 是否转发成功
     */
    boolean forwardPluginApi(HttpServletRequest request, HttpServletResponse response,
                                    String pluginCode, JWT jwt, Long domainId);

    /**
     * 验证插件API权限
     *
     * @param request HTTP请求
     * @param pluginCode 插件代码
     * @param jwt JWT令牌
     * @param domainId 域名ID
     * @return 是否有权限
     */
    boolean verifyPluginApiAuth(HttpServletRequest request, String pluginCode, JWT jwt, Long domainId);

    String isPluginApiRequest(String uri);
}
