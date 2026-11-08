/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.audit.support;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wldos.gateway.GatewayClientIpResolver;
import io.github.wldos.framework.support.audit.SystemEvent;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 系统日志 metadata 补全客户端 IP（HTTP 请求上下文可用时）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class SystemLogIpEnricher {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private SystemLogIpEnricher() {
	}

	public static void enrichIfAbsent(SystemEvent event) {
		if (event == null) {
			return;
		}
		HttpServletRequest request = currentRequest();
		if (request == null) {
			return;
		}
		String ip = GatewayClientIpResolver.resolve(request);
		event.setMetadata(mergeRequestContext(event.getMetadata(), request, ip));
	}

	public static String extractIpFromMetadata(String metadata) {
		if (metadata == null || metadata.isEmpty()) {
			return null;
		}
		try {
			JsonNode node = MAPPER.readTree(metadata);
			if (node.hasNonNull("ip")) {
				String ip = node.get("ip").asText();
				return ip.isEmpty() ? null : ip;
			}
		}
		catch (Exception ignored) {
			// metadata 非 JSON 时不解析
		}
		return null;
	}

	private static boolean hasIp(String metadata) {
		return extractIpFromMetadata(metadata) != null;
	}

	private static String mergeRequestContext(String metadata, HttpServletRequest request, String ip) {
		try {
			ObjectNode node;
			if (metadata == null || metadata.isEmpty()) {
				node = MAPPER.createObjectNode();
			}
			else {
				JsonNode parsed = MAPPER.readTree(metadata);
				node = parsed.isObject() ? (ObjectNode) parsed : MAPPER.createObjectNode().set("data", parsed);
			}
			if (ip != null && !ip.isEmpty() && !node.hasNonNull("ip")) {
				node.put("ip", ip);
			}
			putIfAbsentText(node, "uri", request.getRequestURI());
			putIfAbsentText(node, "method", request.getMethod());
			putIfAbsentText(node, "host", request.getHeader("Host"));
			String scheme = request.getHeader("X-Forwarded-Proto");
			if (scheme == null || scheme.isEmpty()) {
				scheme = request.getScheme();
			}
			putIfAbsentText(node, "scheme", scheme);
			int port = request.getServerPort();
			if (port > 0 && !node.hasNonNull("port")) {
				node.put("port", port);
			}
			return MAPPER.writeValueAsString(node);
		}
		catch (Exception ignored) {
			return "{\"ip\":\"" + ip.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
		}
	}

	private static void putIfAbsentText(ObjectNode node, String key, String value) {
		if (value == null || value.isEmpty() || node.hasNonNull(key)) {
			return;
		}
		node.put(key, value);
	}

	private static HttpServletRequest currentRequest() {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		if (attrs instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes) attrs).getRequest();
		}
		return null;
	}
}
