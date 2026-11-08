/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * 网关拒绝写系统日志时的请求上下文。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Getter
@Setter
public class EdgeRejectionContext {

	private final String clientIp;
	private final String reqUri;
	private final String userId;
	private final String method;
	private final String query;
	private String host;
	private String userAgent;
	private String referer;
	private String actorKind;
	private String actorReason;

	public EdgeRejectionContext(String clientIp, String reqUri, String userId, String method, String query) {
		this.clientIp = clientIp;
		this.reqUri = reqUri;
		this.userId = userId;
		this.method = method;
		this.query = query;
	}

	public static EdgeRejectionContext of(HttpServletRequest request, String clientIp, String reqUri, String userId) {
		String method = request == null ? null : request.getMethod();
		String query = request == null ? null : truncateQuery(request.getQueryString());
		EdgeRejectionContext ctx = new EdgeRejectionContext(clientIp, reqUri, userId, method, query);
		if (request != null) {
			ctx.setHost(resolveHost(request));
			ctx.setUserAgent(truncate(request.getHeader("User-Agent"), 512));
			ctx.setReferer(truncate(request.getHeader("Referer"), 256));
		}
		return ctx;
	}

	private static String resolveHost(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-Host");
		if (forwarded != null && !forwarded.isEmpty()) {
			int comma = forwarded.indexOf(',');
			return truncate(comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim(), 128);
		}
		return truncate(request.getHeader("Host"), 128);
	}

	static String truncateQuery(String query) {
		return truncate(query, 200);
	}

	private static String truncate(String s, int max) {
		if (s == null) {
			return null;
		}
		if (s.length() <= max) {
			return s;
		}
		return s.substring(0, max);
	}
}
