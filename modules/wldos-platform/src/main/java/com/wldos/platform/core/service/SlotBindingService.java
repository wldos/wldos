/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.SlotBindingDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoSlotBinding;
import com.wldos.platform.core.vo.SlotBindingBody;
import com.wldos.platform.core.vo.SlotBindingVO;
import com.wldos.platform.core.vo.SlotCatalogVO;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.rbac.slot.BusinessSlot;
import io.github.wldos.platform.support.rbac.slot.SlotResolver;
import io.github.wldos.platform.support.rbac.slot.SlotTargetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 插槽绑定：超管管全平台，租户/站点管理员管本租户；变更后刷新解析缓存。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
public class SlotBindingService extends NonEntityService {

	@Autowired
	private SlotBindingDao slotBindingDao;

	@Autowired
	private OrgDao orgRepo;

	@Autowired
	private RoleDao roleRepo;

	@Autowired
	private DomainDao domainRepo;

	@Autowired
	private SlotResolver slotResolver;

	@Autowired
	@Lazy
	private ResourceService resourceService;

	/** 插槽注册表（标注当前租户或站点是否已绑定；选定站点时按该站点生效口径标注：站点级+租户级） */
	public List<SlotCatalogVO> listCatalog(Long comId, Long domainId) {
		Long scopeComId = this.assertScopeAndResolveComId(comId);
		Long scopeDomainId = this.assertDomainInScope(scopeComId, domainId);
		Set<String> boundCodes = this.slotBindingDao.findValidByCom(scopeComId).stream()
				.filter(b -> this.inSiteView(b, scopeDomainId))
				.map(WoSlotBinding::getSlotCode).collect(Collectors.toSet());
		return Arrays.stream(BusinessSlot.values()).map(slot -> {
			SlotCatalogVO vo = new SlotCatalogVO();
			vo.setSlotCode(slot.getSlotCode());
			vo.setTargetType(slot.getTargetType().name().toLowerCase());
			vo.setDefaultKind(slot.getDefaultKind());
			vo.setDescription(slot.getDescription());
			vo.setBound(boundCodes.contains(slot.getSlotCode()));
			return vo;
		}).collect(Collectors.toList());
	}

	/** 当前租户或站点的绑定清单（含目标与站点名称；选定站点时仅列该站点生效的绑定：站点级+租户级） */
	public List<SlotBindingVO> listBindings(Long comId, Long domainId) {
		Long scopeComId = this.assertScopeAndResolveComId(comId);
		Long scopeDomainId = this.assertDomainInScope(scopeComId, domainId);
		List<WoSlotBinding> bindings = this.slotBindingDao.findValidByCom(scopeComId).stream()
				.filter(b -> this.inSiteView(b, scopeDomainId))
				.collect(Collectors.toList());
		List<SlotBindingVO> rows = new ArrayList<>(bindings.size());
		for (WoSlotBinding binding : bindings) {
			SlotBindingVO vo = new SlotBindingVO();
			vo.setId(binding.getId());
			vo.setSlotCode(binding.getSlotCode());
			BusinessSlot slot = BusinessSlot.ofSlotCode(binding.getSlotCode());
			vo.setSlotDescription(slot != null ? slot.getDescription() : binding.getSlotCode());
			vo.setTargetType(binding.getTargetType());
			vo.setComId(binding.getComId());
			vo.setDomainId(binding.getDomainId() != null && binding.getDomainId() > 0 ? binding.getDomainId() : null);
			vo.setTargetId(binding.getTargetId());
			if (vo.getDomainId() != null) {
				WoDomain domain = this.domainRepo.findById(vo.getDomainId()).orElse(null);
				vo.setDomainName(domain != null ? domain.getSiteName() : null);
			}
			vo.setTargetName(this.resolveTargetName(binding.getTargetType(), binding.getTargetId()));
			rows.add(vo);
		}
		return rows;
	}

	/** 绑定插槽到分群/角色；同租户同插槽重复绑定则改绑 */
	@Transactional(rollbackFor = Exception.class)
	public void bind(SlotBindingBody body) {
		if (body == null || ObjectUtils.isBlank(body.getSlotCode()) || body.getTargetId() == null) {
			throw new BaseException("插槽编码与绑定目标不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		BusinessSlot slot = BusinessSlot.ofSlotCode(body.getSlotCode());
		if (slot == null) {
			throw new BaseException("插槽编码未注册: " + body.getSlotCode(), ResultCode.VALIDATION_ERROR.getCode());
		}
		if (slot.getTargetType() == SlotTargetType.ROLE_SET) {
			throw new BaseException("角色集插槽暂不支持绑定（按默认实现解析）", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainId = body.getDomainId() != null && body.getDomainId() > 0 ? body.getDomainId() : 0L;
		WoDomain domain = null;
		if (domainId > 0) {
			domain = this.domainRepo.findById(domainId).orElse(null);
			if (domain == null) {
				throw new BaseException("目标站点不存在", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
		// 未选站点时按所选站点推导租户；跨租户站点会被权限校验拒绝
		Long comId = this.assertScopeAndResolveComId(body.getComId() != null && body.getComId() > 0
				? body.getComId() : (domain != null ? domain.getComId() : null));
		if (domain != null && !Objects.equals(domain.getComId() != null ? domain.getComId() : Constants.TOP_COM_ID, comId)) {
			throw new BaseException("目标站点不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		String targetName = this.assertTargetValid(slot.getTargetType(), body.getTargetId(), comId, domainId);
		if (ObjectUtils.isBlank(targetName)) {
			throw new BaseException("绑定目标不存在或不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (BusinessSlot.GUEST_ROLE.equals(slot)) {
			this.resourceService.assertGuestRoleSlotTarget(body.getTargetId(), domainId, comId);
		}

		Timestamp now = DateUtils.convSQLDate(new java.util.Date());
		WoSlotBinding existing = this.slotBindingDao.findValidBySlotAndCom(slot.getSlotCode(), comId).stream()
				.filter(b -> Objects.equals(b.getDomainId() != null ? b.getDomainId() : 0L, domainId))
				.findFirst().orElse(null);
		if (existing != null) {
			existing.setTargetId(body.getTargetId());
			existing.setTargetType(slot.getTargetType().name().toLowerCase());
			existing.setUpdateBy(this.commonOperate.getUserId());
			existing.setUpdateIp(this.commonOperate.getUserIp());
			existing.setUpdateTime(now);
			this.slotBindingDao.save(existing);
		}
		else {
			WoSlotBinding binding = new WoSlotBinding();
			binding.setId(this.nextId());
			binding.setSlotCode(slot.getSlotCode());
			binding.setComId(comId);
			binding.setDomainId(domainId);
			binding.setTargetType(slot.getTargetType().name().toLowerCase());
			binding.setTargetId(body.getTargetId());
			binding.setIsValid(ValidStatusEnum.VALID.toString());
			binding.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			binding.setCreateBy(this.commonOperate.getUserId());
			binding.setCreateIp(this.commonOperate.getUserIp());
			binding.setCreateTime(now);
			this.slotBindingDao.save(binding);
		}
		this.slotResolver.refreshSlot(comId, domainId, slot.getSlotCode());
		// 角色插槽直接改变游客/会员鉴权解析，分群插槽改变新用户挂组后的组织角色继承，都须联动清授权缓存
		this.refreshAuth();
	}

	/** 解绑（逻辑删除） */
	@Transactional(rollbackFor = Exception.class)
	public void unbind(Long id) {
		if (id == null) {
			throw new BaseException("绑定id不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoSlotBinding binding = this.slotBindingDao.findById(id).orElse(null);
		if (binding == null || !DeleteFlagEnum.NORMAL.toString().equals(binding.getDeleteFlag())) {
			return;
		}
		this.assertScopeAndResolveComId(binding.getComId());
		binding.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		binding.setUpdateBy(this.commonOperate.getUserId());
		binding.setUpdateIp(this.commonOperate.getUserIp());
		binding.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
		this.slotBindingDao.save(binding);
		this.slotResolver.refreshSlot(binding.getComId(), binding.getDomainId(), binding.getSlotCode());
		this.refreshAuth();
	}

	/**
	 * 校验管理范围并解析生效租户：超管任意；租户/站点管理员固定本租户。
	 */
	private Long assertScopeAndResolveComId(Long requestedComId) {
		Long userId = this.commonOperate.getUserId();
		if (this.commonOperate.isAdmin(userId)) {
			return requestedComId != null ? requestedComId : Constants.TOP_COM_ID;
		}
		boolean tenantAdmin = this.commonOperate.isTenantAdmin(userId);
		boolean domainAdmin = this.commonOperate.isDomainAdmin(userId);
		if (!tenantAdmin && !domainAdmin) {
			throw new BaseException("无权管理插槽绑定", ResultCode.FORBIDDEN.getCode());
		}
		Long tenantId = this.commonOperate.getOperatorComId(userId);
		if (tenantId == null || tenantId <= 0) {
			throw new BaseException("无权管理插槽绑定", ResultCode.FORBIDDEN.getCode());
		}
		if (requestedComId != null && requestedComId > 0 && !requestedComId.equals(tenantId)) {
			throw new BaseException("无权管理其他租户的插槽绑定", ResultCode.FORBIDDEN.getCode());
		}
		return tenantId;
	}

	/** 校验站点归属当前租户或站点并返回规范化站点id（空或0归一为 null 表示不按站点过滤） */
	private Long assertDomainInScope(Long scopeComId, Long domainId) {
		if (domainId == null || domainId <= 0) {
			return null;
		}
		WoDomain domain = this.domainRepo.findById(domainId).orElse(null);
		if (domain == null) {
			throw new BaseException("目标站点不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long domainComId = domain.getComId();
		if (domainComId == null) {
			throw new BaseException("目标站点未绑定租户", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!Objects.equals(domainComId, scopeComId)) {
			throw new BaseException("目标站点不属于当前租户或站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		return domainId;
	}

	/** 站点视角下的生效口径：站点级绑定优先，租户级绑定兜底 */
	private boolean inSiteView(WoSlotBinding binding, Long scopeDomainId) {
		if (scopeDomainId == null) {
			return true;
		}
		Long rowDomainId = binding.getDomainId() != null ? binding.getDomainId() : 0L;
		return rowDomainId <= 0 || scopeDomainId.equals(rowDomainId);
	}

	/** 校验绑定目标存在且归属当前租户或站点（站点级绑定时目标须属该站点），返回目标名称 */
	private String assertTargetValid(SlotTargetType targetType, Long targetId, Long comId, Long domainId) {
		if (targetType == SlotTargetType.ORG) {
			WoOrg org = this.orgRepo.findById(targetId).orElse(null);
			if (org == null || !DeleteFlagEnum.NORMAL.toString().equals(org.getDeleteFlag())
					|| !Objects.equals(org.getComId(), comId)) {
				return null;
			}
			if (domainId > 0 && org.getDomainId() != null && org.getDomainId() > 0 && !domainId.equals(org.getDomainId())) {
				return null;
			}
			return org.getOrgName();
		}
		WoRole role = this.roleRepo.findById(targetId).orElse(null);
		if (role == null || !DeleteFlagEnum.NORMAL.toString().equals(role.getDeleteFlag())
				|| !Objects.equals(role.getComId(), comId)) {
			return null;
		}
		if (domainId > 0 && role.getDomainId() != null && role.getDomainId() > 0 && !domainId.equals(role.getDomainId())) {
			return null;
		}
		return role.getRoleName();
	}

	private String resolveTargetName(String targetType, Long targetId) {
		if (targetId == null) {
			return null;
		}
		if (SlotTargetType.ORG.name().equalsIgnoreCase(targetType)) {
			WoOrg org = this.orgRepo.findById(targetId).orElse(null);
			return org != null ? org.getOrgName() : null;
		}
		WoRole role = this.roleRepo.findById(targetId).orElse(null);
		return role != null ? role.getRoleName() : null;
	}
}
