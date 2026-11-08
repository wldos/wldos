/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 典型扫描探针 URI 前缀匹配；配置项 {@code wldos.gateway.probe.*}，支持 wo_options 热刷。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
public class EdgeGatewayProbeMatcher {

	public static final String PROP_ENABLED = "wldos.gateway.probe.enabled";
	public static final String PROP_PATHS = "wldos.gateway.probe.paths";
	public static final String PROP_AUDIT_ENABLED = "wldos.gateway.probe.audit-enabled";

	private final Environment environment;

	public EdgeGatewayProbeMatcher(Environment environment) {
		this.environment = environment;
	}

	public boolean isEnabled() {
		return environment.getProperty(PROP_ENABLED, Boolean.class, Boolean.FALSE);
	}

	public boolean isAuditEnabled() {
		return environment.getProperty(PROP_AUDIT_ENABLED, Boolean.class, Boolean.FALSE);
	}

	public List<String> probePrefixes() {
		return probePrefixesInternal();
	}

	public boolean matches(String reqUri) {
		if (!isEnabled() || ObjectUtils.isBlank(reqUri)) {
			return false;
		}
		List<String> prefixes = probePrefixesInternal();
		if (prefixes.isEmpty()) {
			return false;
		}
		return GateWayHelper.isMatchUri(reqUri, prefixes);
	}

	private List<String> probePrefixesInternal() {
		String raw = environment.getProperty(PROP_PATHS);
		if (!StringUtils.hasText(raw)) {
			return Collections.emptyList();
		}
		String[] parts = raw.split(",");
		List<String> list = new ArrayList<>(parts.length);
		for (String part : parts) {
			String p = part.trim();
			if (!p.isEmpty()) {
				list.add(p);
			}
		}
		return list;
	}
}
