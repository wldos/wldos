/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 启动时从 {@link RequestMappingHandlerMapping} 构建的只读路由索引：精确 path + 字面前缀分桶 + 兜底桶。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
final class EdgeRoutePatternIndex {

	private static final AntPathMatcher MATCHER = new AntPathMatcher();

	private static final int BUCKET_LITERAL_SEGMENTS = 2;

	final int patternCount;

	final int exactCount;

	final int bucketCount;

	final int fallbackCount;

	private final Set<String> exactPaths;

	private final Map<String, List<String>> buckets;

	private final List<String> fallbackPatterns;

	private EdgeRoutePatternIndex(Set<String> exactPaths, Map<String, List<String>> buckets,
			List<String> fallbackPatterns) {
		this.exactPaths = exactPaths;
		this.buckets = buckets;
		this.fallbackPatterns = fallbackPatterns;
		this.exactCount = exactPaths.size();
		this.bucketCount = buckets.size();
		this.fallbackCount = fallbackPatterns.size();
		this.patternCount = exactCount + sumListSizes(buckets) + fallbackCount;
	}

	static EdgeRoutePatternIndex build(ApplicationContext applicationContext) {
		Set<String> exact = new HashSet<>(256);
		Map<String, List<String>> bucketMap = new HashMap<>(128);
		List<String> fallback = new ArrayList<>(32);
		Set<String> seen = new HashSet<>(4096);

		Map<String, RequestMappingHandlerMapping> beans =
				applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);
		for (RequestMappingHandlerMapping mapping : beans.values()) {
			Map<RequestMappingInfo, ?> all;
			try {
				all = mapping.getHandlerMethods();
			}
			catch (Exception e) {
				continue;
			}
			for (RequestMappingInfo info : all.keySet()) {
				Collection<String> patterns = patternsOf(info);
				if (patterns == null) {
					continue;
				}
				for (String pattern : patterns) {
					if (pattern == null || pattern.isEmpty() || !seen.add(pattern)) {
						continue;
					}
					registerPattern(pattern, exact, bucketMap, fallback);
				}
			}
		}
		return new EdgeRoutePatternIndex(
				Collections.unmodifiableSet(exact),
				Collections.unmodifiableMap(bucketMap),
				Collections.unmodifiableList(fallback));
	}

	boolean matches(String uri) {
		if (uri == null || uri.isEmpty()) {
			return false;
		}
		if (exactPaths.contains(uri)) {
			return true;
		}
		List<String> candidates = collectCandidates(uri);
		for (String pattern : candidates) {
			if (pattern.equals(uri)) {
				return true;
			}
			try {
				if (MATCHER.match(pattern, uri)) {
					return true;
				}
			}
			catch (IllegalArgumentException ignore) {
				// 个别 pattern 编译失败，跳过
			}
		}
		return false;
	}

	private List<String> collectCandidates(String uri) {
		List<String> keys = uriBucketKeys(uri, BUCKET_LITERAL_SEGMENTS);
		if (keys.isEmpty()) {
			return fallbackPatterns;
		}
		int estimated = fallbackPatterns.size() + keys.size() * 4;
		List<String> out = new ArrayList<>(Math.min(estimated, 512));
		Set<String> dedupe = new HashSet<>(Math.min(estimated, 512));
		for (String key : keys) {
			List<String> list = buckets.get(key);
			if (list == null) {
				continue;
			}
			for (String p : list) {
				if (dedupe.add(p)) {
					out.add(p);
				}
			}
		}
		for (String p : fallbackPatterns) {
			if (dedupe.add(p)) {
				out.add(p);
			}
		}
		return out;
	}

	private static void registerPattern(String pattern, Set<String> exact, Map<String, List<String>> bucketMap,
			List<String> fallback) {
		int wild = firstWildcard(pattern);
		if (wild < 0) {
			exact.add(pattern);
			return;
		}
		if (wild == 0) {
			fallback.add(pattern);
			return;
		}
		String literalPrefix = trimTrailingSlash(pattern.substring(0, wild));
		if (literalPrefix.isEmpty() || "/".equals(literalPrefix)) {
			fallback.add(pattern);
			return;
		}
		String bucketKey = literalBucketKey(literalPrefix, BUCKET_LITERAL_SEGMENTS);
		if (bucketKey == null) {
			fallback.add(pattern);
			return;
		}
		bucketMap.computeIfAbsent(bucketKey, k -> new ArrayList<>(8)).add(pattern);
	}

	private static int firstWildcard(String pattern) {
		int brace = pattern.indexOf('{');
		int star = pattern.indexOf('*');
		if (brace < 0) {
			return star;
		}
		if (star < 0) {
			return brace;
		}
		return Math.min(brace, star);
	}

	private static String trimTrailingSlash(String path) {
		while (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	static String literalBucketKey(String literalPrefix, int maxSegments) {
		if (literalPrefix == null || literalPrefix.isEmpty() || "/".equals(literalPrefix)) {
			return null;
		}
		if (!literalPrefix.startsWith("/")) {
			literalPrefix = "/" + literalPrefix;
		}
		String[] parts = literalPrefix.split("/");
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (int i = 1; i < parts.length && count < maxSegments; i++) {
			if (parts[i].isEmpty()) {
				continue;
			}
			sb.append('/').append(parts[i]);
			count++;
		}
		return count == 0 ? null : sb.toString();
	}

	static List<String> uriBucketKeys(String uri, int maxSegments) {
		if (uri == null || uri.isEmpty() || !uri.startsWith("/")) {
			return Collections.emptyList();
		}
		String path = uri;
		int q = path.indexOf('?');
		if (q >= 0) {
			path = path.substring(0, q);
		}
		path = trimTrailingSlash(path);
		if (path.isEmpty() || "/".equals(path)) {
			return Collections.emptyList();
		}
		String[] parts = path.split("/");
		List<String> keys = new ArrayList<>(maxSegments);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (int i = 1; i < parts.length && count < maxSegments; i++) {
			if (parts[i].isEmpty()) {
				continue;
			}
			sb.append('/').append(parts[i]);
			count++;
			keys.add(sb.toString());
		}
		return keys;
	}

	private static int sumListSizes(Map<String, List<String>> bucketMap) {
		int n = 0;
		for (List<String> list : bucketMap.values()) {
			n += list.size();
		}
		return n;
	}

	private static Collection<String> patternsOf(RequestMappingInfo info) {
		PatternsRequestCondition pc = info.getPatternsCondition();
		if (pc != null) {
			return pc.getPatterns();
		}
		try {
			Object ppc = RequestMappingInfo.class.getMethod("getPathPatternsCondition").invoke(info);
			if (ppc != null) {
				Object patterns = ppc.getClass().getMethod("getPatternValues").invoke(ppc);
				if (patterns instanceof Collection) {
					@SuppressWarnings("unchecked")
					Collection<String> c = (Collection<String>) patterns;
					return c;
				}
			}
		}
		catch (ReflectiveOperationException ignore) {
			// 老 Spring 没有该方法，忽略
		}
		return null;
	}
}
