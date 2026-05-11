/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.audit;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.framework.support.audit.IOpLogContextResolver;
import io.github.wldos.framework.support.audit.ResourceTypeDict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * CMS 模块的操作日志名称解析器。
 *
 * <p>覆盖 {@code @OpLog} 中的内容资源类型，分两类处理：
 * <ul>
 *   <li><b>有名称回填</b>：
 *     <ul>
 *       <li>{@code info / pub / book / chapter} → {@code k_pubs.pub_title}（同表，仅 {@code pub_type} 不同）；</li>
 *       <li>{@code comment} → {@code k_comments.content} 截断 30 字符。</li>
 *     </ul>
 *   </li>
 *   <li><b>仅声明 type 不查名</b>（{@code supportedCodes} 仍然包含，确保前端展示中文 label 而非灰色英文 code）：
 *     <ul>
 *       <li>{@code info_cover / chapter_image}：文件上传附属资源，无独立业务记录；</li>
 *       <li>{@code star}：互动统计表 {@code k_stars}，没有"业务名称"列，由 StarController 走基类 CRUD 时触发。</li>
 *     </ul>
 *     这三种 type 的"资源"列保持空白即可，{@code resourceType} 列展示中文 label。
 *   </li>
 * </ul>
 *
 * @author Yuanxi Universe
 * @date 2026/05/05
 * @version 1.0
 */
@Slf4j
@Component
public class CmsOpLogContextResolver implements IOpLogContextResolver {

	/** {@code k_pubs} 共享一表的多 type 集合，用于 {@code resolveResourceNames} 时合并查询。 */
	private static final Set<CmsResourceType> PUB_TABLE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			CmsResourceType.INFO, CmsResourceType.PUB, CmsResourceType.BOOK, CmsResourceType.CHAPTER
	)));

	/** 评论内容回填长度上限，避免长评论占满日志列。 */
	private static final int COMMENT_PREVIEW_LEN = 30;

	/**
	 * WLDOS 硬约定：依赖 {@link CommonOperation} 的 bean 必须 {@code @Lazy + 字段注入}，
	 * 否则会在 系统启动时 提前实例化，导致 Spring 抛 IllegalStateException：
	 * {@code there is already object [...] bound under bean name 'commonOperation'}。
	 */
	@SuppressWarnings("all")
	@Autowired
	@Lazy
	private CommonOperation commonOperation;

	@Override
	public Set<String> supportedResourceTypes() {
		return CmsResourceType.supportedCodes();
	}

	@Override
	public List<ResourceTypeDict> resourceTypeLabels() {
		return CmsResourceType.dict();
	}

	@Override
	public Map<String, Map<String, String>> resolveResourceNames(Map<String, Set<String>> typeToIds) {
		if (typeToIds == null || typeToIds.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, Map<String, String>> out = new HashMap<>();

		// k_pubs 一表多 type，先合并 info/pub/book/chapter 的所有 id 一起查
		Set<String> rawPubIds = new HashSet<>();
		for (CmsResourceType t : PUB_TABLE_TYPES) {
			Set<String> ids = typeToIds.get(t.getCode());
			if (ids != null) rawPubIds.addAll(ids);
		}
		if (!rawPubIds.isEmpty()) {
			Map<String, String> pubMap = queryPubTitles(rawPubIds);
			if (!pubMap.isEmpty()) {
				// 同一份解析结果回填到所有出现过的 type，简化前端逻辑
				for (CmsResourceType t : PUB_TABLE_TYPES) {
					Set<String> ids = typeToIds.get(t.getCode());
					if (ids == null || ids.isEmpty()) continue;
					Map<String, String> partial = new HashMap<>();
					for (String id : ids) {
						String name = pubMap.get(id);
						if (name != null) partial.put(id, name);
					}
					if (!partial.isEmpty()) out.put(t.getCode(), partial);
				}
			}
		}

		Set<String> commentIds = typeToIds.get(CmsResourceType.COMMENT.getCode());
		if (commentIds != null && !commentIds.isEmpty()) {
			Map<String, String> commentMap = queryCommentPreviews(commentIds);
			if (!commentMap.isEmpty()) {
				out.put(CmsResourceType.COMMENT.getCode(), commentMap);
			}
		}

		return out;
	}

	private Map<String, String> queryPubTitles(Set<String> rawIds) {
		Set<Long> ids = toLongIds(rawIds);
		if (ids.isEmpty()) {
			return Collections.emptyMap();
		}
		String sql = "SELECT id, pub_title AS name FROM k_pubs"
				+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")";
		try {
			List<Map<String, Object>> rows = commonOperation.getJdbcOperations()
					.queryForList(sql, ids.toArray());
			Map<String, String> out = new HashMap<>(rows.size() * 2);
			for (Map<String, Object> r : rows) {
				Object idObj = r.get("id");
				Object nameObj = r.get("name");
				if (idObj == null || nameObj == null) continue;
				out.put(String.valueOf(idObj), String.valueOf(nameObj));
			}
			return out;
		}
		catch (Exception ex) {
			log.warn("queryPubTitles failed: {}", ex.getMessage());
			return Collections.emptyMap();
		}
	}

	private Map<String, String> queryCommentPreviews(Set<String> rawIds) {
		Set<Long> ids = toLongIds(rawIds);
		if (ids.isEmpty()) {
			return Collections.emptyMap();
		}
		String sql = "SELECT id, content FROM k_comments"
				+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")";
		try {
			List<Map<String, Object>> rows = commonOperation.getJdbcOperations()
					.queryForList(sql, ids.toArray());
			Map<String, String> out = new HashMap<>(rows.size() * 2);
			for (Map<String, Object> r : rows) {
				Object idObj = r.get("id");
				Object contentObj = r.get("content");
				if (idObj == null || contentObj == null) continue;
				String preview = truncate(String.valueOf(contentObj), COMMENT_PREVIEW_LEN);
				out.put(String.valueOf(idObj), preview);
			}
			return out;
		}
		catch (Exception ex) {
			log.warn("queryCommentPreviews failed: {}", ex.getMessage());
			return Collections.emptyMap();
		}
	}

	private static String truncate(String s, int max) {
		if (s == null) return null;
		if (s.length() <= max) return s;
		return s.substring(0, max) + "...";
	}

	private static Set<Long> toLongIds(Set<String> rawIds) {
		return rawIds.stream()
				.map(s -> {
					try { return Long.parseLong(s); }
					catch (NumberFormatException ex) { return null; }
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private static String placeholders(int n) {
		StringBuilder sb = new StringBuilder(n * 2);
		for (int i = 0; i < n; i++) {
			if (i > 0) sb.append(',');
			sb.append('?');
		}
		return sb.toString();
	}
}
