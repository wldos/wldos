/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.auth.clickcaptcha;

import java.util.Locale;

import io.github.wldos.common.utils.ObjectUtils;

/**
 * 从显式 locale 参数或 Accept-Language 解析点选字池键。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class ClickCaptchaLocaleResolver {

	private ClickCaptchaLocaleResolver() {
	}

	/**
	 * @param localeParam 请求参数 locale（如 zh-CN、en-US，可为空）
	 * @param acceptLanguage Accept-Language 头（可为空）
	 */
	public static String resolve(String localeParam, String acceptLanguage) {
		String fromParam = normalizeTag(ObjectUtils.string(localeParam).trim());
		if (!ObjectUtils.isBlank(fromParam)) {
			return fromParam;
		}
		String first = firstAcceptLanguageTag(acceptLanguage);
		String fromHeader = normalizeTag(first);
		if (!ObjectUtils.isBlank(fromHeader)) {
			return fromHeader;
		}
		return ClickCaptchaPoolKey.ZH_HANS;
	}

	private static String firstAcceptLanguageTag(String acceptLanguage) {
		if (ObjectUtils.isBlank(acceptLanguage)) {
			return "";
		}
		String segment = acceptLanguage.split(",")[0].trim();
		int semi = segment.indexOf(';');
		if (semi > 0) {
			segment = segment.substring(0, semi).trim();
		}
		return segment;
	}

	private static String normalizeTag(String tag) {
		if (ObjectUtils.isBlank(tag)) {
			return "";
		}
		String lower = tag.replace('_', '-').toLowerCase(Locale.ROOT);
		if (lower.startsWith("en")) {
			return ClickCaptchaPoolKey.EN;
		}
		if (lower.equals("zh-tw") || lower.equals("zh-hk") || lower.equals("zh-mo")
				|| lower.startsWith("zh-hant")) {
			return ClickCaptchaPoolKey.ZH_HANT;
		}
		if (lower.startsWith("zh")) {
			return ClickCaptchaPoolKey.ZH_HANS;
		}
		return "";
	}
}
