/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 加载前端构建产出的页面-API 依赖清单。
 *
 * <p>候选来源（取磁盘上<strong>最新修改时间</strong>的一份；{@code wldos.page-api-manifest.path} 若配置则优先且独占）：
 * <ol>
 *   <li>{@code wldos.page-api-manifest.path}（宿主机显式指定，如 {@code /opt/wldos/ui/wldos/page-api-manifest.json}）</li>
 *   <li>{@code wldos_frontend_ui_dir}/wldos/page-api-manifest.json（含 {@code @/} 解析）</li>
 *   <li>约定部署目录 {@code {platformRoot父级}/ui/wldos/...}</li>
 *   <li>classpath 展开目录 {@code target/classes/wldos/...}（仅 IDE 调试）</li>
 * </ol>
 * 均无有效文件时回退 classpath 流式读取（JAR 内嵌副本）。
 */
@Slf4j
@Component
public class PageApiManifestLoader {

	private static final String MANIFEST_REL_PATH = "wldos/page-api-manifest.json";

	private volatile Map<String, PageApiManifestEntry> cached;

	@Value("${wldos.page-api-manifest.path:}")
	private String manifestPathOverride;

	@Value("${wldos_frontend_ui_dir:}")
	private String frontendUiDir;

	public Map<String, PageApiManifestEntry> load() {
		Map<String, PageApiManifestEntry> local = this.cached;
		if (local != null) {
			return local;
		}
		synchronized (this) {
			if (this.cached != null) {
				return this.cached;
			}
			this.cached = this.readManifest();
			return this.cached;
		}
	}

	/**
	 * 每次重读清单；供检漏等管理操作使用。
	 */
	public Map<String, PageApiManifestEntry> loadFresh() {
		Map<String, PageApiManifestEntry> manifest = this.readManifest();
		this.cached = manifest;
		return manifest;
	}

	private Map<String, PageApiManifestEntry> readManifest() {
		Map<String, PageApiManifestEntry> fromOverride = this.readFromOverridePath();
		if (fromOverride != null) {
			return fromOverride;
		}
		File newest = this.pickNewestManifestFile();
		if (newest != null) {
			Map<String, PageApiManifestEntry> fromFile = this.readFromFile(newest);
			if (fromFile != null) {
				log.info("[page-api-manifest] loaded from file: {} (pages={})", newest.getAbsolutePath(), fromFile.size());
				return fromFile;
			}
		}
		this.logMissingFileCandidates();
		Map<String, PageApiManifestEntry> fromClasspath = this.readFromClasspath();
		log.info("[page-api-manifest] loaded from classpath stream (pages={})", fromClasspath.size());
		return fromClasspath;
	}

	private Map<String, PageApiManifestEntry> readFromOverridePath() {
		if (!StringUtils.hasText(this.manifestPathOverride)) {
			return null;
		}
		File file = new File(this.manifestPathOverride.trim());
		if (!file.isFile()) {
			log.warn("[page-api-manifest] wldos.page-api-manifest.path not found: {}", file.getAbsolutePath());
			return null;
		}
		Map<String, PageApiManifestEntry> manifest = this.readFromFile(file);
		if (manifest != null) {
			log.info("[page-api-manifest] loaded from override path: {} (pages={})", file.getAbsolutePath(), manifest.size());
		}
		return manifest;
	}

	private void logMissingFileCandidates() {
		if (!log.isDebugEnabled()) {
			return;
		}
		for (File candidate : this.collectManifestFileCandidates()) {
			log.debug("[page-api-manifest] candidate {} exists={}", candidate.getAbsolutePath(), candidate.isFile());
		}
	}

	private File pickNewestManifestFile() {
		File best = null;
		long bestTime = -1L;
		for (File candidate : this.collectManifestFileCandidates()) {
			if (!candidate.isFile()) {
				continue;
			}
			long modified = candidate.lastModified();
			if (modified > bestTime) {
				bestTime = modified;
				best = candidate;
			}
		}
		return best;
	}

	private List<File> collectManifestFileCandidates() {
		Set<String> seen = new LinkedHashSet<>();
		List<File> candidates = new ArrayList<>();
		this.addCandidate(candidates, seen, this.resolveConfiguredUiManifest());
		this.addCandidate(candidates, seen, this.resolveDefaultUiManifest());
		this.addCandidate(candidates, seen, this.resolveClasspathExplodedManifest());
		return candidates;
	}

	private void addCandidate(List<File> candidates, Set<String> seen, File file) {
		if (file == null) {
			return;
		}
		String key = file.getAbsolutePath();
		if (seen.add(key)) {
			candidates.add(file);
		}
	}

	private File resolveConfiguredUiManifest() {
		String uiDir = this.resolveFrontendUiDir(this.frontendUiDir);
		if (!StringUtils.hasText(uiDir)) {
			return null;
		}
		return new File(uiDir, MANIFEST_REL_PATH);
	}

	private File resolveDefaultUiManifest() {
		File platformRoot = this.platformRootDir();
		if (platformRoot == null) {
			return null;
		}
		File parent = platformRoot.getParentFile();
		if (parent == null) {
			return null;
		}
		return new File(new File(parent, "ui"), MANIFEST_REL_PATH);
	}

	private File resolveClasspathExplodedManifest() {
		URL url = Thread.currentThread().getContextClassLoader().getResource(MANIFEST_REL_PATH);
		if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
			return null;
		}
		try {
			return new File(url.toURI());
		}
		catch (Exception e) {
			return null;
		}
	}

	private String resolveFrontendUiDir(String configured) {
		if (!StringUtils.hasText(configured)) {
			return null;
		}
		String trimmed = configured.trim();
		if (!trimmed.startsWith("@/")) {
			return trimmed;
		}
		File platformRoot = this.platformRootDir();
		if (platformRoot == null) {
			return null;
		}
		File parent = platformRoot.getParentFile();
		if (parent == null) {
			return null;
		}
		File grand = parent.getParentFile();
		if (grand == null) {
			return null;
		}
		String remaining = trimmed.substring(2).replace('/', File.separatorChar);
		return new File(grand, remaining).getAbsolutePath();
	}

	private File platformRootDir() {
		String root = System.getProperty("wldos.platform.root");
		if (!StringUtils.hasText(root)) {
			return null;
		}
		return new File(root.trim());
	}

	private Map<String, PageApiManifestEntry> readFromFile(File file) {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.readValue(file, new TypeReference<Map<String, PageApiManifestEntry>>() {});
		}
		catch (IOException e) {
			log.warn("[page-api-manifest] read failed: {} ({})", file.getAbsolutePath(), e.getMessage());
			return null;
		}
	}

	private Map<String, PageApiManifestEntry> readFromClasspath() {
		ObjectMapper om = new ObjectMapper();
		try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(MANIFEST_REL_PATH)) {
			if (in == null) {
				return Collections.emptyMap();
			}
			return om.readValue(in, new TypeReference<Map<String, PageApiManifestEntry>>() {});
		}
		catch (IOException e) {
			log.warn("[page-api-manifest] classpath read failed: {}", e.getMessage());
			return Collections.emptyMap();
		}
	}

	@Getter
	@Setter
	public static class PageApiManifestEntry {
		private String component;
		private List<PageApiRef> apis;
	}

	@Getter
	@Setter
	public static class PageApiRef {
		private String method;
		private String path;
	}

	public static String apiKey(String method, String path) {
		String m = method == null ? "GET" : method.trim().toUpperCase();
		String p = path == null ? "" : path.trim();
		return m + ":" + p;
	}
}
