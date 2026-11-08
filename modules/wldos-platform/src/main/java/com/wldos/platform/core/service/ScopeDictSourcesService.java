/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.ScopeDimensionDao;
import com.wldos.platform.core.dict.DictRegistry;
import com.wldos.platform.core.entity.WoScopeDimension;
import com.wldos.platform.core.support.ScopeDimensionCatalogHelper;
import com.wldos.platform.core.vo.ScopeDictSourceVO;
import com.wldos.platform.core.vo.TermTypeMeta;

import io.github.wldos.common.vo.SelectOption;

/**
 * 汇总 DictRegistry 与类型管理，列出可登记为业务字段的字典源。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
@Transactional(readOnly = true)
public class ScopeDictSourcesService extends NonEntityService {

	private final ScopeDimensionDao scopeDimensionDao;

	@Autowired
	@Lazy
	private TermTypeMetaService termTypeMetaService;

	public ScopeDictSourcesService(ScopeDimensionDao scopeDimensionDao) {
		this.scopeDimensionDao = scopeDimensionDao;
	}

	public List<ScopeDictSourceVO> listDictSources() {
		Map<String, ScopeDictSourceVO> merged = new LinkedHashMap<>();
		for (Map.Entry<String, Supplier<List<SelectOption>>> entry : DictRegistry.enumSourcesView().entrySet()) {
			putSource(merged, entry.getKey(), DictRegistry.enumLabel(entry.getKey()), "ENUM");
		}
		for (TermTypeMeta meta : this.termTypeMetaService.findAllTermTypes()) {
			if (meta == null || !StringUtils.hasText(meta.getCode())) {
				continue;
			}
			String dictCode = StringUtils.hasText(meta.getDictCode()) ? meta.getDictCode().trim() : meta.getCode().trim();
			String name = StringUtils.hasText(meta.getName()) ? meta.getName().trim() : dictCode;
			putSource(merged, dictCode, name, "TERM");
		}
		Map<String, String> registeredByDict = buildRegisteredDictIndex();
		for (ScopeDictSourceVO item : merged.values()) {
			String dimCode = registeredByDict.get(item.getDictCode());
			if (dimCode != null) {
				item.setRegistered(Boolean.TRUE);
				item.setRegisteredDimensionCode(dimCode);
			} else {
				item.setRegistered(Boolean.FALSE);
			}
		}
		return new ArrayList<>(merged.values());
	}

	public String resolveDictName(String dictCode) {
		if (!StringUtils.hasText(dictCode)) {
			return "";
		}
		String code = dictCode.trim();
		for (ScopeDictSourceVO item : listDictSources()) {
			if (code.equals(item.getDictCode())) {
				return item.getName();
			}
		}
		return code;
	}

	private static void putSource(Map<String, ScopeDictSourceVO> merged, String dictCode, String name, String sourceKind) {
		if (!StringUtils.hasText(dictCode)) {
			return;
		}
		String key = dictCode.trim();
		ScopeDictSourceVO existing = merged.get(key);
		if (existing != null) {
			if (!StringUtils.hasText(existing.getName()) && StringUtils.hasText(name)) {
				existing.setName(name);
			}
			return;
		}
		ScopeDictSourceVO item = new ScopeDictSourceVO();
		item.setDictCode(key);
		item.setName(StringUtils.hasText(name) ? name : key);
		item.setSourceKind(sourceKind);
		merged.put(key, item);
	}

	private Map<String, String> buildRegisteredDictIndex() {
		Map<String, String> index = new LinkedHashMap<>();
		List<WoScopeDimension> rows = this.scopeDimensionDao.findAllDictDimensions();
		if (rows == null) {
			return index;
		}
		for (WoScopeDimension row : rows) {
			String dictCode = extractDictCode(row.getValueSource());
			if (dictCode != null && StringUtils.hasText(row.getDimensionCode())) {
				index.put(dictCode, row.getDimensionCode());
			}
		}
		return index;
	}

	public static String extractDictCode(String valueSource) {
		if (!StringUtils.hasText(valueSource)) {
			return null;
		}
		String source = valueSource.trim();
		if (source.startsWith(ScopeDimensionCatalogHelper.VALUE_SOURCE_DICT_PREFIX)) {
			return source.substring(ScopeDimensionCatalogHelper.VALUE_SOURCE_DICT_PREFIX.length()).trim();
		}
		if (source.startsWith(ScopeDimensionCatalogHelper.VALUE_SOURCE_TERM_CLASS_PREFIX)) {
			return source.substring(ScopeDimensionCatalogHelper.VALUE_SOURCE_TERM_CLASS_PREFIX.length()).trim();
		}
		return null;
	}
}
