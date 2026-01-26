/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.gateway.plugins.impl;

import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.framework.support.web.EdgeHandler;
import com.wldos.framework.support.web.FilterRequestWrapper;
import com.wldos.gateway.plugins.IPluginApiGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Set;

/**
 * 默认 IPluginApiGateway 实现类（开源版本）
 * 当 Agent 模块中的 PluginApiGateway 不存在时使用此实现
 * 
 * 开源版本支持基础插件生态（插件可以加载和运行），但不支持高级插件引擎功能：
 * - ✅ 支持：基础插件加载、插件API路由、插件运行
 * - ❌ 不支持：热插拔、插件隔离、Spring上下文隔离、Hook系统等高级功能
 * 
 * 注意：要实现完整的插件生态，需要安装商业版的 agent 模块
 * 
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@ConditionalOnMissingClass("com.wldos.support.plugins.gateway.PluginApiGateway")
@Component
@Slf4j
public class DefaultPluginApiGatewayImpl implements IPluginApiGateway {

	@Autowired
	@Lazy
	private EdgeHandler edgeHandler;

	// 开源版本：简单的插件代码集合（不支持热插拔，需要重启应用）
	private final Set<String> installedPluginCodes = Collections.synchronizedSet(new java.util.HashSet<>());

	@Override
	public Set<String> getInstalledPluginCodes() {
		// 返回已安装的插件代码集合（开源版本：静态集合，不支持热插拔）
		return Collections.unmodifiableSet(installedPluginCodes);
	}

	@Override
	public boolean forwardPluginApi(HttpServletRequest request, HttpServletResponse response,
	                                String pluginCode, JWT jwt, Long domainId) {
		// 开源版本：基础插件API转发支持
		// 注意：这需要插件已经通过其他方式（如手动部署）加载到应用中
		if (pluginCode == null || !installedPluginCodes.contains(pluginCode)) {
			log.warn("插件未安装或未加载: pluginCode={}, uri={}", 
				pluginCode, request.getRequestURI());
			return false;
		}

		// 开源版本：简单的请求转发（不支持插件隔离、Spring上下文隔离等高级功能）
		try {
			String originalUri = request.getRequestURI();
			
			// 处理请求头，添加必要的上下文信息（userId、tenantId、domainId等）
			// 这样插件Controller才能获取到必要的信息
			FilterRequestWrapper wrappedRequest = null;
			if (edgeHandler != null && jwt != null) {
				wrappedRequest = edgeHandler.handleRequest(request, jwt, domainId, "");
			}
			
			// 使用包装后的请求进行转发（如果edgeHandler不可用，使用原始请求）
			HttpServletRequest requestToForward = wrappedRequest != null ? wrappedRequest : request;
			
			// 直接转发到插件Controller（需要插件已注册到主应用的Spring上下文）
			requestToForward.getRequestDispatcher(originalUri).forward(requestToForward, response);
			return true;
		} catch (Exception e) {
			log.error("插件API转发失败（开源版本仅支持基础转发）: pluginCode={}, uri={}, error={}", 
				pluginCode, request.getRequestURI(), e.getMessage());
			return false;
		}
	}

	@Override
	public boolean verifyPluginApiAuth(HttpServletRequest request, String pluginCode, JWT jwt, Long domainId) {
		// 开源版本：基础权限验证（简化实现）
		if (pluginCode == null || !installedPluginCodes.contains(pluginCode)) {
			return false;
		}
		// 开源版本：简单的权限验证（不支持细粒度的插件权限管理）
		return jwt != null && jwt.getUserId() != null;
	}

	@Override
	public String isPluginApiRequest(String uri) {
		// 开源版本：基础插件API识别
		if (uri != null && uri.startsWith("/plugins/")) {
			String prefix = "/plugins/";
			int startIndex = prefix.length();
			int endIndex = uri.indexOf('/', startIndex);
			if (endIndex > startIndex) {
				String pluginCode = uri.substring(startIndex, endIndex);
				// 检查插件是否已安装（开源版本：静态检查）
				if (installedPluginCodes.contains(pluginCode)) {
					return pluginCode;
				}
			}
		}
		return null;
	}

	/**
	 * 注册插件代码（开源版本：手动注册，不支持热插拔）
	 * 注意：此方法主要用于兼容性，实际插件加载需要通过其他机制（如手动部署）
	 * 
	 * @param pluginCode 插件代码
	 */
	public void registerPluginCode(String pluginCode) {
		if (pluginCode != null && !pluginCode.isEmpty()) {
			installedPluginCodes.add(pluginCode);
			log.info("注册插件代码（开源版本，不支持热插拔）: pluginCode={}", pluginCode);
		}
	}

	/**
	 * 注销插件代码（开源版本：手动注销，不支持热插拔）
	 * 
	 * @param pluginCode 插件代码
	 */
	public void unregisterPluginCode(String pluginCode) {
		if (pluginCode != null) {
			installedPluginCodes.remove(pluginCode);
			log.info("注销插件代码（开源版本，不支持热插拔）: pluginCode={}", pluginCode);
		}
	}
}
