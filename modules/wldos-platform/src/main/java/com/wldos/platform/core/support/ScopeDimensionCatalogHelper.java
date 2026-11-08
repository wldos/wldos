/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.support;

import org.springframework.util.StringUtils;

import com.wldos.platform.core.entity.WoScopeDimension;

/**
 * 范围维 catalog 分类（内置 vs 字典；全局字段 vs 定制字段）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class ScopeDimensionCatalogHelper {

	/** 全局字段（库列 scope_ui_tier） */
	public static final String SCOPE_TIER_GLOBAL = "global";

	/** 定制字段（库列 scope_ui_tier，存值为 extension） */
	public static final String SCOPE_TIER_CUSTOM = "extension";

	public static final String VALUE_SOURCE_DICT_PREFIX = "DICT:";

	public static final String VALUE_SOURCE_TERM_CLASS_PREFIX = "TERM_CLASS:";

	public static final String DEFAULT_DIMENSION_CODE_SUFFIX = "_code";

	public static final String ENABLED_YES = "1";

	public static final String ENABLED_NO = "0";

	private ScopeDimensionCatalogHelper() {
	}

	public static String formatDictValueSource(String dictCode) {
		return VALUE_SOURCE_DICT_PREFIX + dictCode;
	}

	public static boolean isEnabled(String enabled) {
		return ENABLED_YES.equals(enabled);
	}

	public static String resolveEnabledFlag(String raw) {
		return ENABLED_NO.equals(raw) ? ENABLED_NO : ENABLED_YES;
	}

	public static boolean isDictValueSource(String valueSource) {
		if (!StringUtils.hasText(valueSource)) {
			return false;
		}
		String source = valueSource.trim();
		return source.startsWith(VALUE_SOURCE_DICT_PREFIX)
				|| source.startsWith(VALUE_SOURCE_TERM_CLASS_PREFIX);
	}

	public static boolean isDictDimension(WoScopeDimension dimension) {
		return dimension != null && isDictValueSource(dimension.getValueSource());
	}

	public static boolean isBuiltinFilterDimension(WoScopeDimension dimension) {
		return dimension != null && !isDictDimension(dimension);
	}

	public static boolean isGlobalDimension(WoScopeDimension dimension) {
		if (dimension == null || !isEnabled(dimension.getEnabled())) {
			return false;
		}
		if (isBuiltinFilterDimension(dimension)) {
			return true;
		}
		if (!isDictDimension(dimension)) {
			return false;
		}
		return SCOPE_TIER_GLOBAL.equalsIgnoreCase(resolveScopeTier(dimension));
	}

	public static boolean isCustomDimension(WoScopeDimension dimension) {
		if (dimension == null || !isDictDimension(dimension)) {
			return false;
		}
		return SCOPE_TIER_CUSTOM.equalsIgnoreCase(resolveScopeTier(dimension));
	}

	public static String resolveScopeTier(WoScopeDimension dimension) {
		if (dimension == null) {
			return SCOPE_TIER_CUSTOM;
		}
		if (isBuiltinFilterDimension(dimension)) {
			return SCOPE_TIER_GLOBAL;
		}
		if (!StringUtils.hasText(dimension.getScopeUiTier())) {
			return SCOPE_TIER_CUSTOM;
		}
		return dimension.getScopeUiTier().trim();
	}

	public static String buildDimensionCode(String dictCode, String suffix) {
		String effectiveSuffix = StringUtils.hasText(suffix) ? suffix : DEFAULT_DIMENSION_CODE_SUFFIX;
		if (dictCode.endsWith(DEFAULT_DIMENSION_CODE_SUFFIX)) {
			return dictCode;
		}
		return dictCode + effectiveSuffix;
	}
}
