/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

/**
 * edge 拒绝 metadata JSON 拼装。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class EdgeRejectionMetadataBuilder {

	private EdgeRejectionMetadataBuilder() {
	}

	public static String build(EdgeRejectionContext ctx) {
		return build(ctx, 1, ctx == null ? null : ctx.getReqUri());
	}

	public static String build(EdgeRejectionContext ctx, int count, String sampleUri) {
		if (ctx == null) {
			return "{}";
		}
		return build(ctx.getClientIp(), ctx.getReqUri(), ctx.getUserId(), ctx.getMethod(), ctx.getQuery(),
				ctx.getHost(), ctx.getUserAgent(), ctx.getReferer(), ctx.getActorKind(), ctx.getActorReason(),
				count, sampleUri);
	}

	public static String build(String ip, String uri, String userId, String method, String query) {
		return build(ip, uri, userId, method, query, null, null, null, null, null, 1, uri);
	}

	public static String build(String ip, String uri, String userId, String method, String query,
			int count, String sampleUri) {
		return build(ip, uri, userId, method, query, null, null, null, null, null, count, sampleUri);
	}

	public static String build(String ip, String uri, String userId, String method, String query,
			String host, String userAgent, String referer, String actorKind, String actorReason,
			int count, String sampleUri) {
		StringBuilder sb = new StringBuilder(280);
		sb.append("{\"ip\":\"").append(safeJson(ip)).append('"');
		sb.append(",\"uri\":\"").append(safeJson(uri)).append('"');
		sb.append(",\"userId\":\"").append(safeJson(userId)).append('"');
		appendOptional(sb, "method", method);
		appendOptional(sb, "query", query);
		appendOptional(sb, "host", host);
		appendOptional(sb, "userAgent", userAgent);
		appendOptional(sb, "referer", referer);
		appendOptional(sb, "actorKind", actorKind);
		appendOptional(sb, "actorReason", actorReason);
		if (count > 1) {
			sb.append(",\"count\":").append(count);
			if (sampleUri != null && !sampleUri.isEmpty()) {
				sb.append(",\"sampleUri\":\"").append(safeJson(sampleUri)).append('"');
			}
		}
		sb.append('}');
		return sb.toString();
	}

	private static void appendOptional(StringBuilder sb, String key, String value) {
		if (value != null && !value.isEmpty()) {
			sb.append(",\"").append(key).append("\":\"").append(safeJson(value)).append('"');
		}
	}

	private static String safeJson(String s) {
		if (s == null) {
			return "";
		}
		StringBuilder out = new StringBuilder(s.length() + 8);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\\':
					out.append("\\\\");
					break;
				case '"':
					out.append("\\\"");
					break;
				case '\b':
					out.append("\\b");
					break;
				case '\f':
					out.append("\\f");
					break;
				case '\n':
					out.append("\\n");
					break;
				case '\r':
					out.append("\\r");
					break;
				case '\t':
					out.append("\\t");
					break;
				default:
					if (c < 0x20) {
						out.append(String.format("\\u%04x", (int) c));
					} else {
						out.append(c);
					}
					break;
			}
		}
		return out.toString();
	}
}
