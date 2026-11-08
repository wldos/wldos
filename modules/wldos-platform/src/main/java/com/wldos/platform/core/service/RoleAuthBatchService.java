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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.entity.WoAuthRole;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.vo.RoleAuthBatchApplyResultVO;
import com.wldos.platform.core.vo.RoleAuthBatchBody;
import com.wldos.platform.core.vo.RoleAuthBatchPreviewItem;
import com.wldos.platform.core.vo.RoleAuthBatchPreviewVO;
import com.wldos.platform.core.vo.RoleAuthBatchSnapshot;
import com.wldos.platform.core.vo.RoleAuthBatchSnapshotEntry;
import com.wldos.platform.core.vo.RoleAuthDriftItem;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.common.IDGen;
import io.github.wldos.framework.support.cache.ICache;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.kind.SystemRoleKind;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员角色授权批量推送到多站，以及只读差异对比。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class RoleAuthBatchService {

	private static final String PREVIEW_CACHE_PREFIX = "wldos:role-batch:preview:";
	private static final String SNAPSHOT_CACHE_PREFIX = "wldos:role-batch:snapshot:";
	private static final long PREVIEW_TTL_HOURS = 2L;
	/** ConMapCache 仅支持 HOURS/MINUTES/MILLISECONDS，7 天 = 168 小时 */
	private static final long SNAPSHOT_TTL_HOURS = 168L;

	private final AuthRoleDao authRoleDao;

	private final RoleDao roleDao;

	private final DomainDao domainDao;

	private final RoleOperationalKindBridge roleOperationalKindBridge;

	private final RoleService roleService;

	private final DomainBootstrap domainBootstrap;

	private final ICache cache;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public RoleAuthBatchService(
			AuthRoleDao authRoleDao,
			RoleDao roleDao,
			DomainDao domainDao,
			RoleOperationalKindBridge roleOperationalKindBridge,
			RoleService roleService,
			DomainBootstrap domainBootstrap,
			ICache cache) {
		this.authRoleDao = authRoleDao;
		this.roleDao = roleDao;
		this.domainDao = domainDao;
		this.roleOperationalKindBridge = roleOperationalKindBridge;
		this.roleService = roleService;
		this.domainBootstrap = domainBootstrap;
		this.cache = cache;
	}

	public RoleAuthBatchPreviewVO preview(RoleAuthBatchBody body, Long userId, Long tenantId, boolean platformAdmin, boolean tenantAdmin, List<Long> boundDomainIds) {
		WoRole source = resolveSourceRole(body, tenantId);
		assertOperationalBatchAllowed(source);
		assertTargetDomains(body.getTargetDomainIds(), tenantId, platformAdmin, tenantAdmin, boundDomainIds);

		Set<Long> sourceResIds = loadResourceIds(source.getId());
		RoleAuthBatchPreviewVO vo = new RoleAuthBatchPreviewVO();
		vo.setSourceRoleId(source.getId());
		vo.setRoleCode(source.getRoleCode());
		vo.setSourceCount(sourceResIds.size());

		String mode = normalizeMode(body.getMode());
		for (Long domainId : body.getTargetDomainIds()) {
			if (domainId == null || domainId <= 0) {
				continue;
			}
			RoleAuthBatchPreviewItem item = new RoleAuthBatchPreviewItem();
			item.setDomainId(domainId);
			item.setDomainName(resolveDomainName(domainId));
			WoRole target = locateTargetRole(source.getRoleCode(), domainId, tenantId, false);
			if (target == null) {
				item.setInstanceReady(false);
				item.setStatusNote("本站尚无该会员角色实例，已尝试自动补齐仍缺失");
				vo.getItems().add(item);
				continue;
			}
			Set<Long> existing = loadResourceIds(target.getId());
			Set<Long> merged = computeMerged(existing, sourceResIds, mode);
			item.setTargetRoleId(target.getId());
			item.setCurrentCount(existing.size());
			item.setToAdd(countAdd(existing, merged));
			item.setToRemove(countRemove(existing, merged));
			item.setAfterCount(merged.size());
			vo.getItems().add(item);
		}
		if (vo.getItems().stream().anyMatch(i -> !i.isInstanceReady())) {
			return vo;
		}
		String previewToken = String.valueOf(IDGen.nextId());
		this.cache.set(PREVIEW_CACHE_PREFIX + previewToken, buildPreviewCacheValue(body, userId),
				PREVIEW_TTL_HOURS, TimeUnit.HOURS);
		vo.setPreviewToken(previewToken);
		return vo;
	}

	@Transactional
	public RoleAuthBatchApplyResultVO apply(RoleAuthBatchBody body, Long userId, String userIp, Long tenantId, boolean platformAdmin, boolean tenantAdmin, List<Long> boundDomainIds) {
		WoRole source = resolveSourceRole(body, tenantId);
		assertOperationalBatchAllowed(source);
		assertTargetDomains(body.getTargetDomainIds(), tenantId, platformAdmin, tenantAdmin, boundDomainIds);
		this.assertPreviewToken(body, userId);

		String mode = normalizeMode(body.getMode());
		if ("replace".equals(mode) && !Boolean.TRUE.equals(body.getConfirmReplace())) {
			throw new BaseException("replace 模式须 confirmReplace=true", ResultCode.VALIDATION_ERROR.getCode());
		}

		Set<Long> sourceResIds = loadResourceIds(source.getId());
		RoleAuthBatchSnapshot snapshot = new RoleAuthBatchSnapshot();
		snapshot.setSnapshotId(String.valueOf(IDGen.nextId()));
		snapshot.setOperatorUserId(userId);
		snapshot.setTenantId(tenantId);

		for (Long domainId : body.getTargetDomainIds()) {
			if (domainId == null || domainId <= 0) {
				continue;
			}
			WoRole target = resolveTargetRole(source.getRoleCode(), domainId, tenantId);
			if (target == null) {
				throw new BaseException("目标站点尚未初始化角色实例：" + resolveDomainName(domainId), ResultCode.VALIDATION_ERROR.getCode());
			}
			Set<Long> existing = loadResourceIds(target.getId());
			RoleAuthBatchSnapshotEntry entry = new RoleAuthBatchSnapshotEntry();
			entry.setDomainId(domainId);
			entry.setRoleId(target.getId());
			entry.setResourceIds(new ArrayList<>(existing));
			snapshot.getEntries().add(entry);

			Set<Long> merged = computeMerged(existing, sourceResIds, mode);
			List<String> mergedStrings = merged.stream().map(String::valueOf).collect(Collectors.toList());
			this.roleService.authRole(mergedStrings, target.getId(), userId, userIp);
		}
		this.saveSnapshot(snapshot);
		this.cache.remove(PREVIEW_CACHE_PREFIX + body.getPreviewToken());
		RoleAuthBatchApplyResultVO result = new RoleAuthBatchApplyResultVO();
		result.setSnapshotId(snapshot.getSnapshotId());
		List<String> domainLabels = new ArrayList<>(snapshot.getEntries().size());
		for (RoleAuthBatchSnapshotEntry entry : snapshot.getEntries()) {
			if (entry.getDomainId() != null) {
				domainLabels.add(resolveDomainName(entry.getDomainId()));
			}
		}
		result.setSnapshotDomainNames(domainLabels);
		return result;
	}

	@Transactional
	public void rollback(String snapshotId, Long userId, String userIp, Long tenantId, boolean platformAdmin, boolean tenantAdmin, List<Long> boundDomainIds) {
		if (ObjectUtils.isBlank(snapshotId)) {
			throw new BaseException("snapshotId 不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!platformAdmin && !tenantAdmin) {
			throw new BaseException("无权回滚批量推送", ResultCode.FORBIDDEN.getCode());
		}
		RoleAuthBatchSnapshot snapshot = this.loadSnapshot(snapshotId);
		if (snapshot == null) {
			throw new BaseException("快照不存在或已过期/已回滚", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!platformAdmin && !userId.equals(snapshot.getOperatorUserId())) {
			throw new BaseException("仅推送操作人或平台管理员可回滚该快照", ResultCode.FORBIDDEN.getCode());
		}
		List<Long> domainIds = snapshot.getEntries().stream()
				.map(RoleAuthBatchSnapshotEntry::getDomainId)
				.collect(Collectors.toList());
		assertTargetDomains(domainIds, tenantId, platformAdmin, tenantAdmin, boundDomainIds);

		for (RoleAuthBatchSnapshotEntry entry : snapshot.getEntries()) {
			if (entry.getRoleId() == null) {
				continue;
			}
			List<String> resIds = entry.getResourceIds().stream().map(String::valueOf).collect(Collectors.toList());
			this.roleService.authRole(resIds, entry.getRoleId(), userId, userIp);
		}
		this.cache.remove(SNAPSHOT_CACHE_PREFIX + snapshotId);
	}

	public List<RoleAuthDriftItem> driftCompare(String roleCode, Long baselineDomainId, Long tenantId, boolean platformAdmin, boolean tenantAdmin, List<Long> boundDomainIds) {
		if (roleCode == null || roleCode.trim().isEmpty()) {
			throw new BaseException("roleCode 不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!GrantVisibilityPolicy.isOperationalRoleCode(roleCode)) {
			throw new BaseException("仅支持内置会员角色", ResultCode.VALIDATION_ERROR.getCode());
		}

		WoRole baselineRole;
		if (baselineDomainId != null && baselineDomainId > 0) {
			baselineRole = locateTargetRole(roleCode, baselineDomainId, tenantId, false);
			if (baselineRole == null) {
				throw new BaseException("基准站点尚无该角色实例，请换基准站或留空使用平台模板", ResultCode.VALIDATION_ERROR.getCode());
			}
		} else {
			baselineRole = this.roleDao.findPlatformTemplateByRoleCode(roleCode);
		}
		if (baselineRole == null) {
			throw new BaseException("未找到平台角色模板", ResultCode.VALIDATION_ERROR.getCode());
		}
		Set<Long> baselineRes = loadResourceIds(baselineRole.getId());

		List<Long> domainIds = resolveDriftDomainIds(tenantId, platformAdmin, tenantAdmin, boundDomainIds);
		List<RoleAuthDriftItem> items = new ArrayList<>();
		for (Long domainId : domainIds) {
			RoleAuthDriftItem item = new RoleAuthDriftItem();
			item.setDomainId(domainId);
			item.setDomainName(resolveDomainName(domainId));
			WoRole instance = locateTargetRole(roleCode, domainId, tenantId, false);
			if (instance == null) {
				item.setInstanceReady(false);
				item.setStatusNote("本站尚无该会员角色实例，已尝试自动补齐仍缺失");
				items.add(item);
				continue;
			}
			Set<Long> instanceRes = loadResourceIds(instance.getId());
			item.setRoleId(instance.getId());
			item.setInstanceCount(instanceRes.size());
			item.setMissingFromBaseline(countAdd(baselineRes, instanceRes));
			item.setExtraVsBaseline(countAdd(baselineRes, instanceRes));
			items.add(item);
		}
		return items;
	}

	private WoRole resolveSourceRole(RoleAuthBatchBody body, Long tenantId) {
		if (body.getSourceRoleId() != null) {
			return this.roleDao.findById(body.getSourceRoleId())
					.orElseThrow(() -> new BaseException("源角色不存在", ResultCode.VALIDATION_ERROR.getCode()));
		}
		String roleCode = body.getRoleCode();
		if (roleCode == null || roleCode.trim().isEmpty()) {
			throw new BaseException("sourceRoleId 或 roleCode 必填", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (body.getSourceDomainId() != null && body.getSourceDomainId() > 0) {
			WoRole instance = locateTargetRole(roleCode, body.getSourceDomainId(), tenantId, true);
			if (instance == null) {
				throw new BaseException("源站点角色实例不存在", ResultCode.VALIDATION_ERROR.getCode());
			}
			return instance;
		}
		WoRole template = this.roleDao.findPlatformTemplateByRoleCode(roleCode);
		if (template == null) {
			throw new BaseException("未找到平台角色模板", ResultCode.VALIDATION_ERROR.getCode());
		}
		return template;
	}

	private WoRole resolveTargetRole(String roleCode, Long domainId, Long tenantId) {
		WoRole role = locateTargetRole(roleCode, domainId, tenantId, true);
		if (role == null) {
			throw new BaseException("目标站点尚未初始化角色实例：" + resolveDomainName(domainId),
					ResultCode.VALIDATION_ERROR.getCode());
		}
		return role;
	}

	/**
	 * 定位目标站的会员角色实例：先尝试补齐，再解析；strict 为 true 时缺实例则报错。
	 */
	private WoRole locateTargetRole(String roleCode, Long domainId, Long tenantId, boolean strict) {
		if (domainId == null || domainId <= 0) {
			return null;
		}
		Long comId = this.roleOperationalKindBridge.resolveComIdForDomain(domainId, tenantId);
		this.domainBootstrap.ensureSiteOperationalSeed(comId, domainId);
		WoRole catalog = this.roleDao.findPlatformTemplateByRoleCode(roleCode);
		if (catalog == null) {
			return this.roleDao.findByComIdAndDomainIdAndRoleCode(comId, domainId, roleCode);
		}
		if (strict) {
			return this.roleOperationalKindBridge.resolveOperationalInstance(catalog, domainId, comId);
		}
		return this.roleOperationalKindBridge.tryResolveOperationalInstance(catalog, domainId, comId);
	}

	private Set<Long> loadResourceIds(Long roleId) {
		List<WoAuthRole> rows = this.authRoleDao.findAllByRoleId(roleId);
		Set<Long> ids = new LinkedHashSet<>();
		if (rows != null) {
			for (WoAuthRole row : rows) {
				if (row.getResourceId() != null) {
					ids.add(row.getResourceId());
				}
			}
		}
		return ids;
	}

	private Set<Long> computeMerged(Set<Long> existing, Set<Long> source, String mode) {
		if ("replace".equals(mode)) {
			return new LinkedHashSet<>(source);
		}
		Set<Long> merged = new LinkedHashSet<>(existing);
		if ("addMissing".equals(mode)) {
			for (Long id : source) {
				if (!merged.contains(id)) {
					merged.add(id);
				}
			}
			return merged;
		}
		merged.addAll(source);
		return merged;
	}

	private static int countAdd(Set<Long> before, Set<Long> after) {
		int n = 0;
		for (Long id : after) {
			if (!before.contains(id)) {
				n++;
			}
		}
		return n;
	}

	private static int countRemove(Set<Long> before, Set<Long> after) {
		int n = 0;
		for (Long id : before) {
			if (!after.contains(id)) {
				n++;
			}
		}
		return n;
	}

	private static String normalizeMode(String mode) {
		if (mode == null) {
			return "addMissing";
		}
		String m = mode.trim();
		if ("merge".equals(m) || "replace".equals(m) || "addMissing".equals(m)) {
			return m;
		}
		throw new BaseException("mode 须为 addMissing、merge 或 replace", ResultCode.VALIDATION_ERROR.getCode());
	}

	private void assertOperationalBatchAllowed(WoRole source) {
		if (source == null || source.getRoleCode() == null) {
			throw new BaseException("源角色无效", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (GrantVisibilityPolicy.isIdentityRoleCode(source.getRoleCode())) {
			throw new BaseException("平台身份角色不支持批量推送", ResultCode.FORBIDDEN.getCode());
		}
		if (!GrantVisibilityPolicy.isOperationalRoleCode(source.getRoleCode())) {
			throw new BaseException("仅支持内置会员角色批量推送", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private void assertTargetDomains(List<Long> domainIds, Long tenantId, boolean platformAdmin, boolean tenantAdmin, List<Long> boundDomainIds) {
		if (domainIds == null || domainIds.isEmpty()) {
			throw new BaseException("targetDomainIds 不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!platformAdmin && !tenantAdmin) {
			throw new BaseException("无权批量推送授权", ResultCode.FORBIDDEN.getCode());
		}
		Set<Long> allowed = new HashSet<>();
		if (platformAdmin) {
			return;
		}
		if (tenantAdmin && tenantId != null) {
			for (WoDomain domain : this.domainDao.findAllByDeleteFlagAndIsValid("normal", "1")) {
				if (tenantId.equals(domain.getComId())) {
					allowed.add(domain.getId());
				}
			}
		}
		if (boundDomainIds != null) {
			allowed.addAll(boundDomainIds);
		}
		for (Long domainId : domainIds) {
			if (domainId != null && !allowed.contains(domainId)) {
				throw new BaseException("目标站点超出权限范围", ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	private List<Long> resolveDriftDomainIds(Long tenantId, boolean platformAdmin, boolean tenantAdmin, List<Long> boundDomainIds) {
		List<Long> ids = new ArrayList<>();
		for (WoDomain domain : this.domainDao.findAllByDeleteFlagAndIsValid("normal", "1")) {
			if (platformAdmin) {
				ids.add(domain.getId());
				continue;
			}
			if (tenantAdmin && tenantId != null && tenantId.equals(domain.getComId())) {
				ids.add(domain.getId());
			}
		}
		if (!platformAdmin && !tenantAdmin && boundDomainIds != null) {
			ids.addAll(boundDomainIds);
		}
		return ids;
	}

	private String resolveDomainName(Long domainId) {
		return this.domainDao.findById(domainId)
				.map(this::formatDomainDisplayName)
				.orElse(String.valueOf(domainId));
	}

	private String formatDomainDisplayName(WoDomain domain) {
		String baseName = domain.getSiteName();
		if (ObjectUtils.isBlank(baseName)) {
			baseName = domain.getSiteDomain();
		}
		if (ObjectUtils.isBlank(baseName)) {
			baseName = String.valueOf(domain.getId());
		}
		String domainTag = domain.getSiteDomain();
		if (ObjectUtils.isBlank(domainTag)) {
			domainTag = domain.getSecondDomain();
		}
		domainTag = domainTag != null ? domainTag.trim() : "";
		if (!ObjectUtils.isBlank(domainTag) && !domainTag.equalsIgnoreCase(baseName.trim())) {
			return baseName + "（" + domainTag + "）";
		}
		return baseName;
	}

	private void assertPreviewToken(RoleAuthBatchBody body, Long userId) {
		if (ObjectUtils.isBlank(body.getPreviewToken())) {
			throw new BaseException("请先预览 diff，且在修改源/目标/模式前执行推送", ResultCode.VALIDATION_ERROR.getCode());
		}
		String cached = ObjectUtils.string(this.cache.get(PREVIEW_CACHE_PREFIX + body.getPreviewToken()));
		String expected = buildPreviewCacheValue(body, userId);
		if (ObjectUtils.isBlank(cached) || !expected.equals(cached)) {
			throw new BaseException("预览已失效或与当前参数不一致，请重新预览 diff", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private String buildPreviewCacheValue(RoleAuthBatchBody body, Long userId) {
		return buildApplyFingerprint(body) + "|u=" + userId;
	}

	private String buildApplyFingerprint(RoleAuthBatchBody body) {
		StringBuilder sb = new StringBuilder();
		sb.append("sr=").append(body.getSourceRoleId()).append('|');
		sb.append("sd=").append(body.getSourceDomainId()).append('|');
		sb.append("rc=").append(body.getRoleCode()).append('|');
		sb.append("m=").append(normalizeMode(body.getMode())).append('|');
		sb.append("t=");
		List<Long> targetIds = new ArrayList<>(body.getTargetDomainIds() != null ? body.getTargetDomainIds() : Collections.emptyList());
		targetIds.removeIf(id -> id == null || id <= 0);
		Collections.sort(targetIds);
		for (Long id : targetIds) {
			sb.append(id).append(',');
		}
		return sb.toString();
	}

	private void saveSnapshot(RoleAuthBatchSnapshot snapshot) {
		try {
			String json = this.objectMapper.writeValueAsString(snapshot);
			this.cache.set(SNAPSHOT_CACHE_PREFIX + snapshot.getSnapshotId(), json, SNAPSHOT_TTL_HOURS, TimeUnit.HOURS);
		}
		catch (JsonProcessingException ex) {
			throw new BaseException("保存推送快照失败", ResultCode.INTERNAL_ERROR.getCode());
		}
	}

	private RoleAuthBatchSnapshot loadSnapshot(String snapshotId) {
		String json = ObjectUtils.string(this.cache.get(SNAPSHOT_CACHE_PREFIX + snapshotId));
		if (ObjectUtils.isBlank(json)) {
			return null;
		}
		try {
			return this.objectMapper.readValue(json, RoleAuthBatchSnapshot.class);
		}
		catch (JsonProcessingException ex) {
			return null;
		}
	}
}
