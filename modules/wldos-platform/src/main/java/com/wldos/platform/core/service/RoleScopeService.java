/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;
import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.RoleDataPolicyOverlayDao;
import com.wldos.platform.core.dao.RoleDimensionDao;
import com.wldos.platform.core.dao.RoleScopeDao;
import com.wldos.platform.core.dao.ScopeDimensionDao;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoRoleDataPolicyOverlay;
import com.wldos.platform.core.entity.WoRoleDimension;
import com.wldos.platform.core.entity.WoRoleScope;
import com.wldos.platform.core.entity.WoScopeDimension;
import com.wldos.platform.core.support.ScopeDimensionCatalogHelper;
import com.wldos.platform.core.vo.RoleScopeCopyBody;
import com.wldos.platform.core.vo.RoleScopeSaveBody;

import io.github.wldos.platform.support.rbac.EffectiveScopeCache;
import io.github.wldos.platform.support.rbac.JurisdictionOverlayAxis;
import io.github.wldos.platform.support.rbac.OrgClosureQueryHelper;
import io.github.wldos.platform.support.rbac.RoleScopeMode;
import io.github.wldos.platform.support.rbac.RoleScopeMerger;
import io.github.wldos.platform.support.rbac.RoleScopeQueryHelper;
import io.github.wldos.platform.support.rbac.RoleShareScope;
import io.github.wldos.platform.support.rbac.ScopeDimensionCode;
import io.github.wldos.platform.support.rbac.ScopeDimensionRegistry;


/**
 * 角色范围服务
 * 
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleScopeService extends NonEntityService {

	private final RoleScopeDao roleScopeDao;

	private final RoleDimensionDao roleDimensionDao;

	private final RoleDataPolicyOverlayDao dataPolicyOverlayDao;

	private final ScopeDimensionDao scopeDimensionDao;

	private final RoleDao roleDao;

	private final ObjectMapper objectMapper;

	private final JdbcOperations jdbcOperations;

	@Autowired
	@Lazy
	private AdminScopeService adminScopeService;

	@Autowired
	@Lazy
	private ScopeDimensionService scopeDimensionService;

	@Autowired
	@Lazy
	private ScopeDictSourcesService scopeDictSourcesService;

	public RoleScopeService(

			RoleScopeDao roleScopeDao,

			RoleDimensionDao roleDimensionDao,

			RoleDataPolicyOverlayDao dataPolicyOverlayDao,

			ScopeDimensionDao scopeDimensionDao,

			RoleDao roleDao,

			ObjectMapper objectMapper,

			JdbcOperations jdbcOperations) {

		this.roleScopeDao = roleScopeDao;

		this.roleDimensionDao = roleDimensionDao;

		this.dataPolicyOverlayDao = dataPolicyOverlayDao;

		this.scopeDimensionDao = scopeDimensionDao;

		this.roleDao = roleDao;

		this.objectMapper = objectMapper;

		this.jdbcOperations = jdbcOperations;

	}

	public List<WoScopeDimension> listEnabledDimensions() {

		return this.scopeDimensionDao.findAllFilterCatalog();

	}

	public List<WoScopeDimension> listGrantableDimensions(Long grantorId) {

		return this.adminScopeService.grantableScopeDimensions(grantorId);

	}

	public List<WoScopeDimension> listGrantableDimensions(Long grantorId, Long roleId) {

		return this.adminScopeService.grantableScopeDimensions(grantorId, roleId);

	}

	public List<WoScopeDimension> listPickableDimensions(Long grantorId, Long roleId) {

		return this.adminScopeService.pickableScopeDimensions(grantorId, roleId);

	}

	public List<String> listDirectAdoptedDictCodes(Long roleId) {

		if (roleId == null) {
			return Collections.emptyList();
		}

		List<WoScopeDimension> catalog = this.scopeDimensionDao.findAllFilterCatalog();
		Map<String, WoScopeDimension> catalogByCode = catalog.stream()
				.collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));
		Set<String> globalDictCodes = this.adminScopeService.resolveGlobalDictCodes(catalog);

		return this.roleDimensionDao.findByRoleId(roleId).stream()
				.map(WoRoleDimension::getDimensionCode)
				.filter(code -> code != null && !code.isEmpty())
				.map(catalogByCode::get)
				.filter(row -> row != null && ScopeDimensionCatalogHelper.isDictDimension(row))
				.map(row -> ScopeDictSourcesService.extractDictCode(row.getValueSource()))
				.filter(dictCode -> dictCode != null && !globalDictCodes.contains(dictCode))
				.collect(Collectors.toList());
	}

	/** @deprecated 使用 {@link #listDirectAdoptedDictCodes(Long)} */
	public List<String> listDirectAdoptedCodes(Long roleId) {
		return listDirectAdoptedDictCodes(roleId);
	}

	public void saveRoleScopeBundle(Long grantorId, Long roleId, RoleScopeSaveBody body) {

		saveRoleScopeBundle(grantorId, roleId, body, JurisdictionOverlayAxis.baseline());

	}

	public void saveRoleScopeBundle(
			Long grantorId,
			Long roleId,
			RoleScopeSaveBody body,
			JurisdictionOverlayAxis axis) {

		if (roleId == null || body == null) {
			return;
		}

		List<WoRoleScope> scopes = body.getScopes() != null ? body.getScopes() : Collections.emptyList();
		List<String> adoptedDictCodes = resolveAdoptedDictCodes(body);
		List<WoScopeDimension> catalog = this.scopeDimensionDao.findAllFilterCatalog();
		Map<String, String> dictToDimension = resolveAdoptedDimensionMap(adoptedDictCodes);
		List<WoRoleScope> normalizedScopes = normalizeScopeDimensionCodes(scopes, dictToDimension, catalog);
		normalizedScopes = stripDisabledCatalogScopes(normalizedScopes, catalog);

		this.assertAdoptedDimensionsAllowed(roleId, adoptedDictCodes, dictToDimension, normalizedScopes, catalog);
		this.syncDirectAdoptedDimensions(roleId, new ArrayList<>(dictToDimension.values()));
		this.saveRoleScopes(grantorId, roleId, normalizedScopes, axis);
		this.scopeDimensionService.recycleOrphanExtensionCatalogs();
	}

	private List<String> resolveAdoptedDictCodes(RoleScopeSaveBody body) {
		Set<String> codes = new LinkedHashSet<>();
		if (body.getAdoptedDictCodes() != null) {
			for (String dictCode : body.getAdoptedDictCodes()) {
				if (dictCode != null && !dictCode.trim().isEmpty()) {
					codes.add(dictCode.trim());
				}
			}
		}
		if (codes.isEmpty() && body.getAdoptedDimensionCodes() != null && !body.getAdoptedDimensionCodes().isEmpty()) {
			Map<String, String> registeredDict = this.scopeDimensionService.buildRegisteredDictIndexPublic();
			Map<String, String> dimensionToDict = new HashMap<>();
			for (Map.Entry<String, String> entry : registeredDict.entrySet()) {
				dimensionToDict.put(entry.getValue(), entry.getKey());
			}
			for (String dimCode : body.getAdoptedDimensionCodes()) {
				if (dimCode == null || dimCode.trim().isEmpty()) {
					continue;
				}
				String dictCode = dimensionToDict.get(dimCode.trim());
				if (dictCode != null) {
					codes.add(dictCode);
				}
			}
		}
		return new ArrayList<>(codes);
	}

	private Map<String, String> resolveAdoptedDimensionMap(List<String> adoptedDictCodes) {
		Map<String, String> dictToDimension = new LinkedHashMap<>();
		if (adoptedDictCodes == null) {
			return dictToDimension;
		}
		for (String dictCode : adoptedDictCodes) {
			if (dictCode == null || dictCode.trim().isEmpty()) {
				continue;
			}
			String trimmed = dictCode.trim();
			dictToDimension.put(trimmed, this.scopeDimensionService.resolveOrRegisterDimension(trimmed));
		}
		return dictToDimension;
	}

	private List<WoRoleScope> normalizeScopeDimensionCodes(
			List<WoRoleScope> scopes,
			Map<String, String> dictToDimension,
			List<WoScopeDimension> catalog) {
		if (scopes == null || scopes.isEmpty()) {
			return Collections.emptyList();
		}
		Map<String, WoScopeDimension> catalogByCode = catalog.stream()
				.collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));
		Map<String, String> dictByDimension = catalog.stream()
				.filter(ScopeDimensionCatalogHelper::isDictDimension)
				.collect(Collectors.toMap(
						WoScopeDimension::getDimensionCode,
						d -> ScopeDictSourcesService.extractDictCode(d.getValueSource()),
						(a, b) -> a));
		List<WoRoleScope> normalized = new ArrayList<>();
		for (WoRoleScope scope : scopes) {
			if (scope == null) {
				continue;
			}
			WoRoleScope row = scope;
			String code = scope.getDimensionCode();
			if (code != null && !catalogByCode.containsKey(code)) {
				String resolved = dictToDimension.get(code.trim());
				if (resolved != null) {
					row = cloneScopeRow(scope);
					row.setDimensionCode(resolved);
				}
			} else if (code != null && catalogByCode.containsKey(code)) {
				String dictCode = dictByDimension.get(code);
				if (dictCode != null && dictToDimension.containsKey(dictCode)) {
					String resolved = dictToDimension.get(dictCode);
					if (resolved != null && !resolved.equals(code)) {
						row = cloneScopeRow(scope);
						row.setDimensionCode(resolved);
					}
				}
			}
			normalized.add(row);
		}
		return normalized;
	}

	private WoRoleScope cloneScopeRow(WoRoleScope source) {
		WoRoleScope row = new WoRoleScope();
		row.setDimensionCode(source.getDimensionCode());
		row.setScopeMode(source.getScopeMode());
		row.setScopeValues(source.getScopeValues());
		row.setRoleId(source.getRoleId());
		row.setComId(source.getComId());
		row.setDomainId(source.getDomainId());
		return row;
	}

	private List<WoRoleScope> stripDisabledCatalogScopes(
			List<WoRoleScope> scopes,
			List<WoScopeDimension> enabledCatalog) {
		if (scopes == null || scopes.isEmpty()) {
			return Collections.emptyList();
		}
		Set<String> enabledCodes = enabledCatalog.stream()
				.map(WoScopeDimension::getDimensionCode)
				.filter(code -> code != null && !code.isEmpty())
				.collect(Collectors.toSet());
		List<WoRoleScope> kept = new ArrayList<>();
		for (WoRoleScope scope : scopes) {
			if (scope == null || scope.getDimensionCode() == null) {
				continue;
			}
			String code = scope.getDimensionCode();
			if (ScopeDimensionCode.isOperatorDimension(code) || enabledCodes.contains(code)) {
				kept.add(scope);
			}
		}
		return kept;
	}

	public void copyScopesFromRole(Long grantorId, RoleScopeCopyBody body, JurisdictionOverlayAxis axis) {

		if (body == null || body.getSourceRoleId() == null || body.getTargetRoleId() == null) {
			throw new BaseException("源角色与目标角色不能为空");
		}

		Long sourceRoleId = body.getSourceRoleId();
		Long targetRoleId = body.getTargetRoleId();

		if (sourceRoleId.equals(targetRoleId)) {
			throw new BaseException("源角色与目标角色不能相同");
		}

		List<WoRoleScope> sourceScopes = this.listByRoleId(sourceRoleId, axis);
		List<String> sourceAdopts = this.listDirectAdoptedDictCodes(sourceRoleId);

		RoleScopeSaveBody bundle = new RoleScopeSaveBody();
		bundle.setScopes(sourceScopes);
		bundle.setAdoptedDictCodes(sourceAdopts);
		this.saveRoleScopeBundle(grantorId, targetRoleId, bundle, axis);
	}

	private void syncDirectAdoptedDimensions(Long roleId, List<String> adoptedCodes) {

		this.roleDimensionDao.deleteByRoleId(roleId);

		if (adoptedCodes == null || adoptedCodes.isEmpty()) {
			return;
		}

		Set<String> unique = new LinkedHashSet<>();
		for (String code : adoptedCodes) {
			if (code != null && !code.trim().isEmpty()) {
				unique.add(code.trim());
			}
		}

		for (String code : unique) {
			WoRoleDimension row = new WoRoleDimension();
			row.setRoleId(roleId);
			row.setDimensionCode(code);
			this.roleDimensionDao.save(row);
		}
	}

	private void assertAdoptedDimensionsAllowed(
			Long roleId,
			List<String> adoptedDictCodes,
			Map<String, String> dictToDimension,
			List<WoRoleScope> scopes,
			List<WoScopeDimension> catalog) {

		Set<String> globalDictCodes = this.adminScopeService.resolveGlobalDictCodes(catalog);
		Set<String> inheritedDictCodes = resolveInheritedAdoptedDictCodes(roleId, catalog);
		Map<String, WoScopeDimension> catalogByCode = catalog.stream()
				.collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));

		if (adoptedDictCodes != null) {
			for (String dictCode : adoptedDictCodes) {
				if (dictCode == null || dictCode.trim().isEmpty()) {
					continue;
				}
				String trimmed = dictCode.trim();
				this.scopeDictSourcesService.resolveDictName(trimmed);
				if (globalDictCodes.contains(trimmed)) {
					throw new BaseException("该类型已是全局字段: " + trimmed);
				}
				if (inheritedDictCodes.contains(trimmed)) {
					throw new BaseException("定制字段已由父角色继承，无需重复选用: " + trimmed);
				}
			}
		}

		Set<String> allowedDimensionCodes = new HashSet<>(this.adminScopeService.resolveInheritedAdoptedCodes(roleId));
		if (dictToDimension != null) {
			allowedDimensionCodes.addAll(dictToDimension.values());
		}
		for (WoScopeDimension row : catalog) {
			if ("1".equals(row.getEnabled()) && ScopeDimensionCatalogHelper.isDictDimension(row)) {
				String dictCode = ScopeDictSourcesService.extractDictCode(row.getValueSource());
				if (dictCode != null && globalDictCodes.contains(dictCode)) {
					allowedDimensionCodes.add(row.getDimensionCode());
				}
			}
		}
		for (WoScopeDimension row : catalog) {
			if ("1".equals(row.getEnabled()) && ScopeDimensionCatalogHelper.isBuiltinFilterDimension(row)) {
				allowedDimensionCodes.add(row.getDimensionCode());
			}
		}

		if (scopes == null) {
			return;
		}

		for (WoRoleScope scope : scopes) {
			if (scope == null || scope.getDimensionCode() == null) {
				continue;
			}
			WoScopeDimension catalogRow = catalogByCode.get(scope.getDimensionCode());
			if (catalogRow == null || !ScopeDimensionCatalogHelper.isDictDimension(catalogRow)) {
				continue;
			}
			String dictCode = ScopeDictSourcesService.extractDictCode(catalogRow.getValueSource());
			if (dictCode != null && globalDictCodes.contains(dictCode)) {
				continue;
			}
			if (!allowedDimensionCodes.contains(scope.getDimensionCode())) {
				throw new BaseException("请先选用定制字段: " + scope.getDimensionCode());
			}
		}
	}

	private Set<String> resolveInheritedAdoptedDictCodes(Long roleId, List<WoScopeDimension> catalog) {
		Set<String> inheritedDimCodes = this.adminScopeService.resolveInheritedAdoptedCodes(roleId);
		if (inheritedDimCodes.isEmpty()) {
			return Collections.emptySet();
		}
		Map<String, WoScopeDimension> catalogByCode = catalog.stream()
				.collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));
		Set<String> dictCodes = new LinkedHashSet<>();
		for (String dimCode : inheritedDimCodes) {
			WoScopeDimension row = catalogByCode.get(dimCode);
			if (row == null) {
				continue;
			}
			String dictCode = ScopeDictSourcesService.extractDictCode(row.getValueSource());
			if (dictCode != null) {
				dictCodes.add(dictCode);
			}
		}
		return dictCodes;
	}

	public List<WoRoleScope> listByRoleId(Long roleId) {

		return listByRoleId(roleId, JurisdictionOverlayAxis.baseline());

	}

	public List<WoRoleScope> listByRoleId(Long roleId, JurisdictionOverlayAxis axis) {

		if (roleId == null) {

			return Collections.emptyList();

		}

		JurisdictionOverlayAxis effective = axis == null ? JurisdictionOverlayAxis.baseline() : axis;

		return this.roleScopeDao.findByRoleIdAndJurisdiction(
				roleId, effective.getComId(), effective.getDomainId());
	}

	public void saveRoleScopes(Long grantorId, Long roleId, List<WoRoleScope> scopes) {

		saveRoleScopes(grantorId, roleId, scopes, JurisdictionOverlayAxis.baseline());

	}

	public void saveRoleScopes(
			Long grantorId,
			Long roleId,
			List<WoRoleScope> scopes,
			JurisdictionOverlayAxis axis) {

		if (roleId == null) {
			return;
		}

		WoRole role = this.roleDao.findById(roleId).orElse(null);

		JurisdictionOverlayAxis effective = axis == null ? JurisdictionOverlayAxis.baseline() : axis;

		assertSaveAxisAllowed(grantorId, role, effective);

		this.assertScopeWithinGrantor(grantorId, scopes);

		this.roleScopeDao.deleteByRoleIdAndJurisdiction(
				roleId, effective.getComId(), effective.getDomainId());

		if (scopes != null) {

			for (WoRoleScope row : scopes) {

				row.setRoleId(roleId);

				row.setComId(effective.getComId());

				row.setDomainId(effective.getDomainId());

				this.roleScopeDao.save(row);

			}

		}

		EffectiveScopeCache.invalidateAll(this.cache);
	}

	public void saveDataPolicyOverlay(
			Long grantorId,
			Long roleId,
			String dataPolicy,
			JurisdictionOverlayAxis axis) {

		if (roleId == null || axis == null || axis.isBaseline()) {

			return;

		}

		WoRole role = this.roleDao.findById(roleId).orElse(null);

		assertSaveAxisAllowed(grantorId, role, axis);

		this.dataPolicyOverlayDao.deleteByRoleIdAndJurisdiction(

				roleId, axis.getComId(), axis.getDomainId());

		if (dataPolicy != null && !dataPolicy.isEmpty()) {

			WoRoleDataPolicyOverlay row = new WoRoleDataPolicyOverlay();

			row.setRoleId(roleId);

			row.setComId(axis.getComId());

			row.setDomainId(axis.getDomainId());

			row.setDataPolicy(dataPolicy);

			this.dataPolicyOverlayDao.save(row);

		}

		EffectiveScopeCache.invalidateAll(this.cache);
	}


	private void assertSaveAxisAllowed(Long grantorId, WoRole role, JurisdictionOverlayAxis axis) {

		if (role == null || axis == null) {
			return;
		}

		if (RoleShareScope.isShared(role.getShareScope()) && axis.isBaseline()
				&& grantorId != null && !this.isAdmin(grantorId)) {

			throw new BaseException("共享范围模板仅平台超管可改，请在租户或站点下单独配置");

		}
	}

	public void assertScopeWithinGrantor(Long grantorId, List<WoRoleScope> scopes) {

		if (scopes == null || scopes.isEmpty() || grantorId == null) {

			return;

		}

		if (this.isAdmin(grantorId)) {

			return;

		}

		List<String> enabled = this.scopeDimensionDao.findAllFilterCatalog().stream()
				.map(WoScopeDimension::getDimensionCode)
				.collect(Collectors.toList());

		Map<String, List<Long>> grantorDims = RoleScopeQueryHelper.loadMergedDimensions(
				this.jdbcOperations, grantorId, this.getTenantId(), this.getDomainId(), this.bindingChannelEnabled, this.legacyChannelsEnabled);

		Map<String, List<String>> grantorStringDims = RoleScopeQueryHelper.loadMergedStringDimensions(
				this.jdbcOperations, grantorId, this.getTenantId(), this.getDomainId(), this.bindingChannelEnabled, this.legacyChannelsEnabled);

		List<Long> grantorOrgRoots = resolveGrantorOrgRoots(grantorId, grantorDims);

		for (WoRoleScope scope : scopes) {

			if (scope.getDimensionCode() == null || !enabled.contains(scope.getDimensionCode())) {

				throw new BaseException("无效或未登记的范围维: " + scope.getDimensionCode());

			}

			String code = scope.getDimensionCode();

			if (ScopeDimensionCode.isOperatorDimension(code)) {

				continue;

			}

			if (ScopeDimensionRegistry.isStringValueDimension(code)) {

				this.assertStringValuesWithinGrantor(scope, grantorStringDims.get(code));

				continue;

			}

			if (ScopeDimensionCode.ORG_ID.getValue().equals(code)) {

				this.assertOrgScopeWithinGrantor(scope, grantorOrgRoots, grantorId);

				continue;

			}

			this.assertLongValuesWithinGrantor(scope, grantorDims.get(code));

		}
	}

	private void assertOrgScopeWithinGrantor(WoRoleScope scope, List<Long> grantorOrgRoots, Long grantorId) {
		if (RoleScopeMode.isAll(scope.getScopeMode())) {

			if (!grantorOrgRoots.isEmpty()) {

				throw new BaseException("组织范围不能设为全部（超出授权人上限）");

			}
			return;
		}

		if (RoleScopeMode.isValuesScopeMode(scope.getScopeMode())) {

			List<Long> delegateRoots = this.parseScopeValues(scope);
			if (grantorOrgRoots.isEmpty()
					&& this.adminScopeService.resolveLevel(grantorId) == AdminScopeLevel.TENANT) {
				this.assertOrgValuesWithinTenantComId(delegateRoots);
				return;
			}
			this.assertOrgValuesWithinGrantor(delegateRoots, grantorOrgRoots);

		}
	}

	private void assertLongValuesWithinGrantor(WoRoleScope scope, List<Long> grantorValues) {
		if (RoleScopeMode.isAll(scope.getScopeMode())) {

			if (grantorValues != null && !grantorValues.isEmpty()) {

				throw new BaseException("范围不能设为全部（超出授权人上限）: " + scope.getDimensionCode());

			}

			return;

		}

		if (!RoleScopeMode.isValuesScopeMode(scope.getScopeMode())) {

			return;

		}

		List<Long> delegateValues = this.parseScopeValues(scope);

		if (delegateValues.isEmpty()) {

			return;

		}

		if (grantorValues == null || grantorValues.isEmpty()) {

			return;

		}

		for (Long delegateValue : delegateValues) {

			if (delegateValue != null && !grantorValues.contains(delegateValue)) {

				throw new BaseException("范围超出授权人可分配上限: " + scope.getDimensionCode());

			}

		}
	}

	private void assertStringValuesWithinGrantor(WoRoleScope scope, List<String> grantorValues) {

		if (RoleScopeMode.isAll(scope.getScopeMode())) {

			if (grantorValues != null && !grantorValues.isEmpty()) {

				throw new BaseException("范围不能设为全部（超出授权人上限）: " + scope.getDimensionCode());

			}

			return;

		}

		if (!RoleScopeMode.isValuesScopeMode(scope.getScopeMode())) {
			return;
		}

		List<String> delegateValues = this.parseScopeStringValues(scope);

		if (delegateValues.isEmpty()) {
			return;
		}

		if (grantorValues == null || grantorValues.isEmpty()) {
			return;
		}

		for (String delegateValue : delegateValues) {

			if (delegateValue != null && !grantorValues.contains(delegateValue)) {

				throw new BaseException("范围超出授权人可分配上限: " + scope.getDimensionCode());

			}

		}
	}

	private List<Long> resolveGrantorOrgRoots(Long grantorId, Map<String, List<Long>> grantorDims) {

		List<Long> fromRole = grantorDims == null ? null : grantorDims.get(ScopeDimensionCode.ORG_ID.getValue());

		if (fromRole != null && !fromRole.isEmpty()) {

			return fromRole;

		}

		if (this.adminScopeService.resolveLevel(grantorId) == AdminScopeLevel.TENANT) {

			return Collections.emptyList();

		}

		Long primaryOrgId = this.commonOperate.getPrimaryOrgId(grantorId);

		if (primaryOrgId != null) {

			return Collections.singletonList(primaryOrgId);

		}

		return Collections.emptyList();
	}

	private void assertOrgValuesWithinTenantComId(List<Long> delegateOrgIds) {

		if (delegateOrgIds == null || delegateOrgIds.isEmpty()) {

			return;

		}

		Long tenantComId = this.getTenantId();

		if (tenantComId == null || tenantComId <= 0) {

			throw new BaseException("组织范围超出授权人可分配上限");

		}

		for (Long orgId : delegateOrgIds) {

			if (orgId == null) {

				continue;

			}

			Long orgComId = this.jdbcOperations.query(
					"select com_id from wo_org where id = ? and delete_flag = 'normal' limit 1",
					rs -> rs.next() ? rs.getLong("com_id") : null,
					orgId);

			if (orgComId == null || orgComId <= 0 || Objects.equals(orgComId, Constants.TOP_COM_ID)) {

				throw new BaseException("组织范围超出授权人可分配上限");

			}

			if (!tenantComId.equals(orgComId)) {

				throw new BaseException("组织范围超出授权人可分配上限");

			}

		}
	}

	private void assertOrgValuesWithinGrantor(List<Long> delegateRoots, List<Long> grantorRoots) {

		if (delegateRoots == null || delegateRoots.isEmpty()) {

			return;

		}

		if (grantorRoots == null || grantorRoots.isEmpty()) {

			return;

		}

		for (Long delegateRoot : delegateRoots) {

			if (delegateRoot == null) {

				continue;

			}

			if (!OrgClosureQueryHelper.isDescendantOfAny(this.jdbcOperations, delegateRoot, grantorRoots)) {

				throw new BaseException("组织范围超出授权人可分配上限");

			}

		}
	}

	public List<Long> parseScopeValues(WoRoleScope scope) {

		if (scope == null || scope.getScopeValues() == null || scope.getScopeValues().isEmpty()) {

			return Collections.emptyList();

		}

		try {

			return this.objectMapper.readValue(scope.getScopeValues(), new TypeReference<List<Long>>() {});

		}

		catch (Exception ex) {

			throw new BaseException("scope_values 须为 JSON 数组");

		}
	}

	public List<String> parseScopeStringValues(WoRoleScope scope) {

		if (scope == null || scope.getScopeValues() == null || scope.getScopeValues().isEmpty()) {

			return Collections.emptyList();

		}

		return RoleScopeMerger.parseScopeStringValues(scope.getScopeValues());

	}
}