/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.audit.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 从系统日志 metadata JSON 解析列表展示字段。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class SystemLogMetadataEnricher {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private SystemLogMetadataEnricher() {
	}

	public static String extractUriFromMetadata(String metadata) {
		return readText(metadata, "uri", "sampleUri", "lastUri");
	}

	public static String extractMethodFromMetadata(String metadata) {
		return readText(metadata, "method");
	}

	public static String extractHostFromMetadata(String metadata) {
		return readText(metadata, "host");
	}

	public static String extractSchemeFromMetadata(String metadata) {
		String scheme = readText(metadata, "scheme", "xForwardedProto");
		if (scheme != null && !scheme.isEmpty()) {
			return scheme.toLowerCase();
		}
		return null;
	}

	public static Integer extractPortFromMetadata(String metadata) {
		if (metadata == null || metadata.isEmpty()) {
			return null;
		}
		try {
			JsonNode node = MAPPER.readTree(metadata);
			if (node.hasNonNull("port")) {
				return node.get("port").asInt();
			}
			if (node.hasNonNull("serverPort")) {
				return node.get("serverPort").asInt();
			}
		}
		catch (Exception ignored) {
			// metadata 非 JSON
		}
		return null;
	}

	/**
	 * 由 metadata 中的 scheme / host / port / uri 拼完整请求 URL；缺 host 时仅返回 path。
	 */
	public static String formatRequestUrl(String metadata) {
		String path = extractUriFromMetadata(metadata);
		if (path == null || path.isEmpty()) {
			return null;
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String host = extractHostFromMetadata(metadata);
		if (host == null || host.isEmpty()) {
			return path;
		}
		String scheme = extractSchemeFromMetadata(metadata);
		if (scheme == null || scheme.isEmpty()) {
			scheme = "https";
		}
		Integer explicitPort = extractPortFromMetadata(metadata);
		String hostPart = host;
		int port = -1;
		int colon = host.lastIndexOf(':');
		if (colon > 0 && host.indexOf(']') < 0) {
			try {
				port = Integer.parseInt(host.substring(colon + 1));
				hostPart = host.substring(0, colon);
			}
			catch (NumberFormatException ignored) {
				port = -1;
			}
		}
		if (port <= 0 && explicitPort != null && explicitPort > 0) {
			port = explicitPort;
		}
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(hostPart);
		if (port > 0 && !isDefaultPort(scheme, port)) {
			url.append(':').append(port);
		}
		url.append(path);
		return url.toString();
	}

	private static boolean isDefaultPort(String scheme, int port) {
		if ("http".equalsIgnoreCase(scheme)) {
			return port == 80;
		}
		if ("https".equalsIgnoreCase(scheme)) {
			return port == 443;
		}
		return false;
	}

	public static String extractUserAgentFromMetadata(String metadata) {
		return readText(metadata, "userAgent");
	}

	public static String extractRefererFromMetadata(String metadata) {
		return readText(metadata, "referer");
	}

	public static String extractActorKindFromMetadata(String metadata) {
		return readText(metadata, "actorKind");
	}

	public static String extractOperatorUserIdFromMetadata(String metadata) {
		return readText(metadata, "userId", "operatorUserId");
	}

	public static Integer extractAggregateCountFromMetadata(String metadata) {
		if (metadata == null || metadata.isEmpty()) {
			return null;
		}
		try {
			JsonNode node = MAPPER.readTree(metadata);
			if (node.hasNonNull("count")) {
				return node.get("count").asInt(1);
			}
		}
		catch (Exception ignored) {
			// metadata 非 JSON
		}
		return null;
	}

	private static String readText(String metadata, String... keys) {
		if (metadata == null || metadata.isEmpty()) {
			return null;
		}
		try {
			JsonNode node = MAPPER.readTree(metadata);
			for (String key : keys) {
				if (node.hasNonNull(key)) {
					String v = node.get(key).asText();
					if (!v.isEmpty()) {
						return v;
					}
				}
			}
		}
		catch (Exception ignored) {
			// metadata 非 JSON
		}
		return null;
	}
}
