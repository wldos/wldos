/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.wldos.platform.core.dao.SlotBindingDao;
import com.wldos.platform.core.entity.WoSlotBinding;

import io.github.wldos.common.Constants;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.cache.ICache;
import io.github.wldos.platform.support.rbac.kind.KindInstanceResolver;
import io.github.wldos.platform.support.rbac.kind.SystemOrgKind;
import io.github.wldos.platform.support.rbac.kind.SystemRoleKind;
import io.github.wldos.platform.support.rbac.slot.BusinessSlot;
import io.github.wldos.platform.support.rbac.slot.SlotResolver;
import io.github.wldos.platform.support.rbac.slot.SlotTargetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 插槽解析器实现：站点绑定 → 租户绑定 → 插槽平台默认角色。解析结果走 Redis 缓存（1h）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class SlotResolverImpl implements SlotResolver {

	private static final String CACHE_PREFIX = "wldos:slot:";
	private static final long CACHE_HOURS = 1L;

	@Autowired
	private SlotBindingDao slotBindingDao;

	@Autowired
	private KindInstanceResolver kindInstanceResolver;

	@Autowired
	private ICache cache;

	/** 插槽绑定消费开关：false 时跳过绑定表，一律按插槽平台默认角色解析 */
	@Value("${wldos.rbac.slot.enabled:true}")
	private boolean slotEnabled;

	@Override
	public Long resolveOrg(String slotCode, Long comId, Long domainId) {
		List<Long> targets = this.resolveTargets(slotCode, comId, domainId);
		return targets.isEmpty() ? null : targets.get(0);
	}

	@Override
	public Long resolveRole(String slotCode, Long comId, Long domainId) {
		return this.resolveOrg(slotCode, comId, domainId);
	}

	@Override
	public List<Long> resolveTargets(String slotCode, Long comId, Long domainId) {
		BusinessSlot slot = BusinessSlot.ofSlotCode(slotCode);
		if (slot == null) {
			return new ArrayList<>();
		}
		Long tenantId = comId != null ? comId : Constants.TOP_COM_ID;
		String cacheKey = CACHE_PREFIX + tenantId + ":" + (domainId == null ? 0L : domainId) + ":" + slotCode;
		String cached = ObjectUtils.string(this.cache.get(cacheKey));
		if (!ObjectUtils.isBlank(cached)) {
			return parseIds(cached);
		}
		List<Long> targets = this.doResolve(slot, tenantId, domainId);
		this.cache.set(cacheKey, targets.stream().map(String::valueOf).collect(Collectors.joining(",")),
				CACHE_HOURS, TimeUnit.HOURS);
		return targets;
	}

	@Override
	public void refreshSlot(Long comId, Long domainId, String slotCode) {
		Long tenantId = comId != null ? comId : Constants.TOP_COM_ID;
		if (ObjectUtils.isBlank(slotCode)) {
			this.cache.removeByPrefix(CACHE_PREFIX + tenantId + ":");
			return;
		}
		this.cache.remove(CACHE_PREFIX + tenantId + ":" + (domainId == null ? 0L : domainId) + ":" + slotCode);
		// 站点绑定变化不影响租户级缓存键，但租户级绑定变化会影响全部站点解析，一并清
		this.cache.remove(CACHE_PREFIX + tenantId + ":" + 0L + ":" + slotCode);
	}

	private List<Long> doResolve(BusinessSlot slot, Long comId, Long domainId) {
		if (this.slotEnabled) {
			List<WoSlotBinding> bindings = this.slotBindingDao.findValidBySlotAndCom(slot.getSlotCode(), comId);
			Long bound = pickBound(bindings, domainId);
			if (bound != null) {
				List<Long> one = new ArrayList<>();
				one.add(bound);
				return one;
			}
		}
		List<Long> targets = new ArrayList<>();
		for (String kindCode : slot.getDefaultKindCodes()) {
			Long id = null;
			if (slot.getTargetType() == SlotTargetType.ORG) {
				SystemOrgKind kind = SystemOrgKind.ofOrgCode(kindCode);
				if (kind != null) {
					id = this.kindInstanceResolver.resolveOrgKind(comId, domainId, kind);
				}
			}
			else {
				SystemRoleKind kind = SystemRoleKind.ofRoleCode(kindCode);
				if (kind != null) {
					id = this.kindInstanceResolver.resolveRoleKind(comId, domainId, kind);
				}
			}
			if (id != null) {
				targets.add(id);
			}
		}
		return targets;
	}

	/** 站点绑定优先，其次租户级绑定（domain_id 为空或 0）。 */
	private Long pickBound(List<WoSlotBinding> bindings, Long domainId) {
		if (ObjectUtils.isBlank(bindings)) {
			return null;
		}
		Long tenantLevel = null;
		for (WoSlotBinding binding : bindings) {
			boolean siteLevel = binding.getDomainId() != null && binding.getDomainId() > 0;
			if (siteLevel && domainId != null && domainId.equals(binding.getDomainId())) {
				return binding.getTargetId();
			}
			if (!siteLevel) {
				tenantLevel = binding.getTargetId();
			}
		}
		return tenantLevel;
	}

	private List<Long> parseIds(String cached) {
		List<Long> ids = new ArrayList<>();
		for (String part : cached.split(",")) {
			if (!ObjectUtils.isBlank(part)) {
				ids.add(Long.parseLong(part.trim()));
			}
		}
		return ids;
	}
}
