package com.wldos.platform.core.dict;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.github.wldos.common.vo.SelectOption;

/**
 * 统一字典注册表：Java 枚举与 Term 类型双源。
 * 
 * @author Yuanxi Universe (306991142@qq.com)
 * @since 3.0.1
 * @version 1.0.0
 */
public final class DictRegistry {

	private static final Map<String, Supplier<List<SelectOption>>> ENUM_SOURCES = new LinkedHashMap<>();

	/** dictCode → 字典类型展示名（登记业务字段等场景用，非选项 value 的 label） */
	private static final Map<String, String> ENUM_LABELS = new LinkedHashMap<>();

	/** dictCode → k_term_type.class_type（业务字典迁移绑定） */
	private static final Map<String, String> TERM_CLASS_BINDINGS = new LinkedHashMap<>();

	static {
		registerTermBinding("pubStatus", "pub_status");
		registerTermBinding("pluginStatus", "plugin_status");
	}

	private DictRegistry() {
	}

	public static void registerEnum(String code, Supplier<List<SelectOption>> supplier) {
		registerEnum(code, null, supplier);
	}

	public static void registerEnum(String code, String label, Supplier<List<SelectOption>> supplier) {
		if (code == null || supplier == null) {
			return;
		}
		String dictCode = code.trim();
		ENUM_SOURCES.put(dictCode, supplier);
		if (label != null && !label.trim().isEmpty()) {
			ENUM_LABELS.put(dictCode, label.trim());
		}
	}

	public static String enumLabel(String code) {
		if (code == null || code.trim().isEmpty()) {
			return "";
		}
		String dictCode = code.trim();
		String label = ENUM_LABELS.get(dictCode);
		return label != null ? label : dictCode;
	}

	public static void registerTermBinding(String dictCode, String classType) {
		if (dictCode != null && classType != null) {
			TERM_CLASS_BINDINGS.put(dictCode, classType);
		}
	}

	public static Supplier<List<SelectOption>> enumSource(String code) {
		return ENUM_SOURCES.get(code);
	}

	public static String termClassType(String dictCode) {
		return TERM_CLASS_BINDINGS.get(dictCode);
	}

	public static Map<String, Supplier<List<SelectOption>>> enumSourcesView() {
		return Collections.unmodifiableMap(ENUM_SOURCES);
	}

	public static Map<String, String> termBindingsView() {
		return Collections.unmodifiableMap(TERM_CLASS_BINDINGS);
	}
}
