/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.support;

import java.util.Map;

import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.util.StringUtils;

/**
 * 系统日志 metadata JSON 字段的列表筛选 SQL 片段。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class SysLogMetadataQuery {

	private SysLogMetadataQuery() {
	}

	public static String takeConditionString(Map<String, Object> condition, String key) {
		if (condition == null || key == null) {
			return null;
		}
		Object raw = condition.remove(key);
		if (raw == null) {
			return null;
		}
		String v = raw.toString().trim();
		return v.isEmpty() ? null : v;
	}

	public static boolean hasAnyFilter(String requestUri, String requestMethod, String ip, String host,
			String userAgent, String actorKind) {
		return StringUtils.hasText(requestUri) || StringUtils.hasText(requestMethod) || StringUtils.hasText(ip)
				|| StringUtils.hasText(host) || StringUtils.hasText(userAgent) || StringUtils.hasText(actorKind);
	}

	public static void appendMetadataFilters(StringBuilder sql, Map<String, Object> params, String requestUri,
			String requestMethod, String ip, String host, String userAgent, String actorKind) {
		if (StringUtils.hasText(requestUri)) {
			params.put("meta_uri_like", "%" + escapeLike(requestUri.trim()) + "%");
			sql.append(" and a.metadata like :meta_uri_like ");
		}
		if (StringUtils.hasText(requestMethod)) {
			String method = requestMethod.trim().toUpperCase();
			params.put("meta_method_like", "%\"method\":\"" + escapeLike(method) + "\"%");
			sql.append(" and a.metadata like :meta_method_like ");
		}
		if (StringUtils.hasText(ip)) {
			params.put("meta_ip_like", "%\"ip\":\"" + escapeLike(ip.trim()) + "%");
			sql.append(" and a.metadata like :meta_ip_like ");
		}
		if (StringUtils.hasText(host)) {
			params.put("meta_host_like", "%\"host\":\"" + escapeLike(host.trim()) + "%");
			sql.append(" and a.metadata like :meta_host_like ");
		}
		if (StringUtils.hasText(userAgent)) {
			params.put("meta_ua_like", "%" + escapeLike(userAgent.trim()) + "%");
			sql.append(" and a.metadata like :meta_ua_like ");
		}
		if (StringUtils.hasText(actorKind)) {
			String kind = actorKind.trim();
			params.put("meta_actor_kind_like", "%\"actorKind\":\"" + escapeLike(kind) + "\"%");
			sql.append(" and a.metadata like :meta_actor_kind_like ");
		}
	}

	private static String escapeLike(String input) {
		if (ObjectUtils.isBlank(input)) {
			return "";
		}
		return input.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}
}
