/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 */

package com.wldos.cms.query;

import java.util.Map;

import io.github.wldos.common.utils.ObjectUtils;

/**
 * {@code k_pubs} 前台「发现流」SQL 片段与 {@code visibility_scope} 取值归一（CMS 业务层使用，非 SDK 通用能力）。
 */
public final class CmsKpubsDiscoverySql {

	private CmsKpubsDiscoverySql() {
	}

	/** 与 {@link io.github.wldos.common.res.PageQuery#getCondition()} 配合：为 true 时不追加过滤（管理端列表等） */
	public static final String CONDITION_INCLUDE_UNLISTED = "includeUnlistedInList";

	/** {@code k_pubs.visibility_scope} 取值 */
	public static final String SCOPE_PUBLIC_LISTED = "PUBLIC_LISTED";

	public static final String SCOPE_UNLISTED = "UNLISTED";

	public static final String SCOPE_INTERNAL_ONLY = "INTERNAL_ONLY";

	/**
	 * 排除 UNLISTED、INTERNAL_ONLY；NULL/空 视为 PUBLIC_LISTED。
	 */
	public static void appendFrontDiscoveryWhere(StringBuilder sql, String tableAlias, Map<String, Object> condition) {
		if (condition != null && Boolean.TRUE.equals(condition.get(CONDITION_INCLUDE_UNLISTED))) {
			return;
		}
		sql.append(" AND (COALESCE(NULLIF(TRIM(").append(tableAlias).append(".visibility_scope), ''), '")
				.append(SCOPE_PUBLIC_LISTED).append("')")
				.append(" NOT IN ('").append(SCOPE_UNLISTED).append("', '").append(SCOPE_INTERNAL_ONLY).append("')) ");
	}

	/** 写入主表前归一：非法值回落为 {@link #SCOPE_PUBLIC_LISTED}；空为 null。 */
	public static String normalizeScopeForSave(String raw) {
		if (ObjectUtils.isBlank(raw)) {
			return null;
		}
		String t = raw.trim();
		if (SCOPE_PUBLIC_LISTED.equals(t) || SCOPE_UNLISTED.equals(t) || SCOPE_INTERNAL_ONLY.equals(t)) {
			return t;
		}
		return SCOPE_PUBLIC_LISTED;
	}
}
