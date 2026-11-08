/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.platform.support.rbac.ScopeDimensionCode;
import io.github.wldos.platform.support.rbac.ScopeMatchMode;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.ObjectScopeBindDao;
import com.wldos.platform.core.dao.RoleDimensionDao;
import com.wldos.platform.core.dao.RoleScopeDao;
import com.wldos.platform.core.dao.ScopeDimensionDao;
import com.wldos.platform.core.entity.WoScopeDimension;
import com.wldos.platform.core.support.ScopeDimensionCatalogHelper;
import com.wldos.platform.core.vo.ScopeDimensionBatchBody;
import com.wldos.platform.core.vo.ScopeDimensionBatchResultVO;
import com.wldos.platform.core.vo.ScopeDimensionBatchSkipVO;

/**
 * 范围维注册表维护（超管 CRUD）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ScopeDimensionService extends EntityService<ScopeDimensionDao, WoScopeDimension, Long> {

	@Autowired
	private ScopeDictSourcesService scopeDictSourcesService;

	@Autowired
	private RoleScopeDao roleScopeDao;

	@Autowired
	private RoleDimensionDao roleDimensionDao;

	@Autowired
	private ObjectScopeBindDao objectScopeBindDao;

	@Autowired
	private ScopeDimensionLoader scopeDimensionLoader;

	public String resolveOrRegisterDimension(String dictCode) {
		if (!StringUtils.hasText(dictCode)) {
			throw new BaseException("字典类型不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		String trimmed = dictCode.trim();
		this.scopeDictSourcesService.resolveDictName(trimmed);
		Map<String, String> registeredDict = buildRegisteredDictIndex();
		String existing = registeredDict.get(trimmed);
		if (existing != null) {
			WoScopeDimension row = this.entityRepo.findByDimensionCode(existing);
			if (row != null && !ScopeDimensionCatalogHelper.isEnabled(row.getEnabled())) {
				throw new BaseException("业务字段未启用: " + trimmed, ResultCode.VALIDATION_ERROR.getCode());
			}
			return existing;
		}
		String dimensionCode = ScopeDimensionCatalogHelper.buildDimensionCode(trimmed, null);
		if (this.entityRepo.findByDimensionCode(dimensionCode) != null) {
			throw new BaseException("编码 " + dimensionCode + " 已存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		Integer maxSort = this.entityRepo.findMaxSortOrder();
		int nextSort = maxSort != null ? maxSort + 10 : 100;
		WoScopeDimension entity = new WoScopeDimension();
		entity.setDimensionCode(dimensionCode);
		entity.setDimensionName(this.scopeDictSourcesService.resolveDictName(trimmed));
		entity.setValueSource(ScopeDimensionCatalogHelper.formatDictValueSource(trimmed));
		entity.setMatchMode(ScopeMatchMode.IN);
		entity.setEnabled(ScopeDimensionCatalogHelper.ENABLED_YES);
		entity.setScopeUiTier(ScopeDimensionCatalogHelper.SCOPE_TIER_CUSTOM);
		entity.setSortOrder(nextSort);
		this.saveOrUpdate(entity);
		this.scopeDimensionLoader.refresh();
		return dimensionCode;
	}

	public Map<String, String> buildRegisteredDictIndexPublic() {
		return buildRegisteredDictIndex();
	}

	public void assertDeletable(WoScopeDimension entity) {
		assertCatalogReferenceFree(entity, "删除");
	}

	public void assertDisableAllowed(WoScopeDimension entity) {
		assertCatalogReferenceFree(entity, "停用");
	}

	public void recycleOrphanExtensionCatalogs() {
		List<WoScopeDimension> rows = this.entityRepo.findAllDictDimensions();
		if (rows == null || rows.isEmpty()) {
			return;
		}
		boolean changed = false;
		for (WoScopeDimension row : rows) {
			if (row == null || !ScopeDimensionCatalogHelper.isCustomDimension(row)) {
				continue;
			}
			if (!ScopeDimensionCatalogHelper.isEnabled(row.getEnabled())) {
				continue;
			}
			if (hasCatalogReferences(row.getDimensionCode())) {
				continue;
			}
			row.setEnabled(ScopeDimensionCatalogHelper.ENABLED_NO);
			this.saveOrUpdate(row);
			changed = true;
		}
		if (changed) {
			this.scopeDimensionLoader.refresh();
		}
	}

	private void assertCatalogReferenceFree(WoScopeDimension entity, String actionLabel) {
		if (entity == null || !StringUtils.hasText(entity.getDimensionCode())) {
			throw new BaseException("记录不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		String dimensionCode = entity.getDimensionCode();
		if (ScopeDimensionCode.ofCode(dimensionCode) != null) {
			throw new BaseException("内置业务字段不可" + actionLabel + "，请改为未启用", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!ScopeDimensionCatalogHelper.isDictValueSource(entity.getValueSource())) {
			throw new BaseException("仅字典登记的业务字段可" + actionLabel, ResultCode.VALIDATION_ERROR.getCode());
		}
		if (hasCatalogReferences(dimensionCode)) {
			throw new BaseException("已有角色或实体绑定引用，请先清理角色配置后再" + actionLabel, ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private boolean hasCatalogReferences(String dimensionCode) {
		if (!StringUtils.hasText(dimensionCode)) {
			return false;
		}
		if (this.roleScopeDao.countByDimensionCode(dimensionCode) > 0) {
			return true;
		}
		if (this.roleDimensionDao.countByDimensionCode(dimensionCode) > 0) {
			return true;
		}
		return this.objectScopeBindDao.countByDimensionCode(dimensionCode) > 0;
	}

	public ScopeDimensionBatchResultVO batchRegisterFromDict(ScopeDimensionBatchBody body) {
		if (body == null || body.getDictCodes() == null || body.getDictCodes().isEmpty()) {
			throw new BaseException("请选择至少一种字典类型", ResultCode.VALIDATION_ERROR.getCode());
		}
		String suffix = StringUtils.hasText(body.getCodeSuffix())
				? body.getCodeSuffix().trim()
				: ScopeDimensionCatalogHelper.DEFAULT_DIMENSION_CODE_SUFFIX;
		String enabled = ScopeDimensionCatalogHelper.resolveEnabledFlag(body.getEnabled());
		String scopeUiTier = resolveBatchScopeUiTier(body.getScopeUiTier());
		Map<String, String> registeredDict = buildRegisteredDictIndex();
		Integer maxSort = this.entityRepo.findMaxSortOrder();
		int nextSort = maxSort != null ? maxSort + 10 : 100;

		ScopeDimensionBatchResultVO result = new ScopeDimensionBatchResultVO();
		for (String rawCode : body.getDictCodes()) {
			if (!StringUtils.hasText(rawCode)) {
				continue;
			}
			String dictCode = rawCode.trim();
			if (registeredDict.containsKey(dictCode)) {
				appendSkip(result, dictCode, "该字典类型已登记为 " + registeredDict.get(dictCode));
				continue;
			}
			String dimensionCode = ScopeDimensionCatalogHelper.buildDimensionCode(dictCode, suffix);
			if (this.entityRepo.findByDimensionCode(dimensionCode) != null) {
				appendSkip(result, dictCode, "编码 " + dimensionCode + " 已存在");
				continue;
			}
			WoScopeDimension entity = new WoScopeDimension();
			entity.setDimensionCode(dimensionCode);
			entity.setDimensionName(this.scopeDictSourcesService.resolveDictName(dictCode));
			entity.setValueSource(ScopeDimensionCatalogHelper.formatDictValueSource(dictCode));
			entity.setMatchMode(ScopeMatchMode.IN);
			entity.setEnabled(enabled);
			entity.setScopeUiTier(scopeUiTier);
			entity.setSortOrder(nextSort);
			this.saveOrUpdate(entity);
			registeredDict.put(dictCode, dimensionCode);
			result.getCreatedDimensionCodes().add(dimensionCode);
			result.setCreatedCount(result.getCreatedCount() + 1);
			nextSort += 10;
		}
		result.setSkippedCount(result.getSkipped().size());
		return result;
	}

	private static String resolveBatchScopeUiTier(String rawTier) {
		if (!StringUtils.hasText(rawTier)) {
			return ScopeDimensionCatalogHelper.SCOPE_TIER_GLOBAL;
		}
		if (ScopeDimensionCatalogHelper.SCOPE_TIER_CUSTOM.equalsIgnoreCase(rawTier.trim())) {
			return ScopeDimensionCatalogHelper.SCOPE_TIER_CUSTOM;
		}
		return ScopeDimensionCatalogHelper.SCOPE_TIER_GLOBAL;
	}

	private Map<String, String> buildRegisteredDictIndex() {
		Map<String, String> index = new HashMap<>();
		List<WoScopeDimension> rows = this.entityRepo.findAllDictDimensions();
		if (rows == null) {
			return index;
		}
		for (WoScopeDimension row : rows) {
			String dictCode = ScopeDictSourcesService.extractDictCode(row.getValueSource());
			if (dictCode != null && StringUtils.hasText(row.getDimensionCode())) {
				index.put(dictCode, row.getDimensionCode());
			}
		}
		return index;
	}

	private static void appendSkip(ScopeDimensionBatchResultVO result, String dictCode, String reason) {
		ScopeDimensionBatchSkipVO skip = new ScopeDimensionBatchSkipVO();
		skip.setDictCode(dictCode);
		skip.setReason(reason);
		result.getSkipped().add(skip);
	}
}
