/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.TreeUtils;
import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.RoleOrgDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.support.SiteNavDirSupport;
import com.wldos.platform.core.entity.WoAuthRole;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoRoleOrg;
import com.wldos.platform.core.enums.RoleTypeEnum;
import com.wldos.platform.core.vo.AuthRes;
import com.wldos.platform.core.vo.RoleRes;
import com.wldos.platform.core.vo.RoleResTree;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.JurisdictionOverlayAxis;
import io.github.wldos.platform.support.resource.entity.WoResource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理service。
 *
 * @author Yuanxi Universe
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService extends EntityService<RoleDao, WoRole, Long> {

	private final AuthRoleDao authRoleRepo;

	private final ResourceService resourceService;

	private final RoleOrgDao roleOrgRepo;

	private final DomainResourceDao domainResourceDao;

	public RoleService(ResourceService resourceService, AuthRoleDao authRoleRepo, RoleOrgDao roleOrgRepo,
	                   DomainResourceDao domainResourceDao) {
		this.resourceService = resourceService;
		this.authRoleRepo = authRoleRepo;
		this.roleOrgRepo = roleOrgRepo;
		this.domainResourceDao = domainResourceDao;
	}

	/**
	 * 查询角色授予的权限
	 *
	 * @param roleId 角色id
	 * @return 权限列表
	 */
	public List<RoleRes> queryAuthRes(Long roleId) {
		List<WoResource> resources = this.resourceService.queryResourceByRoleId(roleId);

		List<RoleRes> roleResList = resources.parallelStream().map(item -> {
			RoleRes roleRes = new RoleRes();
			roleRes.setId(item.getId());
			roleRes.setInherit(false);
			roleRes.setResourceName(item.getResourceName());
			return roleRes;
		}).collect(Collectors.toList());

		resources = this.resourceService.queryResourceByInheritRoleId(roleId);
		List<RoleRes> resInherit = resources.parallelStream().map(item -> {
			RoleRes roleRes = new RoleRes();
			roleRes.setId(item.getId());
			roleRes.setInherit(true);
			roleRes.setResourceName(item.getResourceName());
			return roleRes;
		}).collect(Collectors.toList());
		roleResList.addAll(resInherit);

		return roleResList;
	}

	/**
	 * 按角色组装授权资源树，对已授权资源分别打标：自有、继承(不能编辑),同时返回已授权资源。
	 *
	 * @param roleId 角色id
	 * @return 权限树
	 */
	public RoleResTree queryAllResTreeByRoleId(Long roleId) {
		return this.queryAllResTreeByRoleId(roleId, null);
	}

	/**
	 * 按角色组装授权资源树，对已授权资源分别打标：自有、继承(不能编辑),同时返回已授权资源。
	 *
	 * @param roleId 角色id
	 * @param allowedResourceIds 允许的资源id集合
	 * @return 权限树
	 */
	public RoleResTree queryAllResTreeByRoleId(Long roleId, java.util.Set<Long> allowedResourceIds) {
		return this.queryAllResTreeByRoleId(roleId, allowedResourceIds, null);
	}

	/**
	 * 按角色组装授权资源树；overlayDomainId 非空时按本站菜单布局展示 parent/order/label（只读 UX）。
	 */
	public RoleResTree queryAllResTreeByRoleId(Long roleId, java.util.Set<Long> allowedResourceIds, Long overlayDomainId) {
		List<WoResource> resources = this.resourceService.listGrantCatalogResources();
		if (allowedResourceIds != null) {
			// 空集合 = 操作者在本站无任何可授资源，候选树应为空而非全量
			resources = resources.stream()
					.filter(res -> allowedResourceIds.contains(res.getId()))
					.collect(Collectors.toList());
		}

		Set<Long> siteMountedNavDirIds = Collections.emptySet();
		if (overlayDomainId != null) {
			List<WoDomainResource> mounts = this.domainResourceDao.queryDomainRes(overlayDomainId);
			if (!ObjectUtils.isBlank(mounts)) {
				siteMountedNavDirIds = mounts.stream()
						.map(WoDomainResource::getResourceId)
						.filter(Objects::nonNull)
						.collect(Collectors.toSet());
			}
		}
		final Set<Long> mountedNavDirIds = siteMountedNavDirIds;
		resources = resources.stream()
				.filter(res -> {
					if (!SiteNavDirSupport.isSiteOwnedNavDir(res)) {
						return true;
					}
					if (overlayDomainId == null) {
						return false;
					}
					return mountedNavDirIds.contains(res.getId());
				})
				.collect(Collectors.toList());

		Map<Long, WoDomainResource> overlayByResId = new HashMap<>();
		if (overlayDomainId != null) {
			List<WoDomainResource> mounts = this.domainResourceDao.queryDomainRes(overlayDomainId);
			if (!ObjectUtils.isBlank(mounts)) {
				for (WoDomainResource mount : mounts) {
					if (mount.getResourceId() != null) {
						overlayByResId.put(mount.getResourceId(), mount);
					}
				}
			}
		}

		List<RoleRes> roleRes = this.queryAuthRes(roleId);

		List<AuthRes> authResList = resources.parallelStream().map(res -> {
			Long parentId = res.getParentId();
			Long displayOrder = res.getDisplayOrder();
			String resourceName = res.getResourceName();
			WoDomainResource overlay = overlayByResId.get(res.getId());
			if (overlay != null) {
				if (overlay.getOverlayParentId() != null) {
					parentId = overlay.getOverlayParentId();
				}
				if (overlay.getOverlayDisplayOrder() != null) {
					displayOrder = overlay.getOverlayDisplayOrder().longValue();
				}
				if (!ObjectUtils.isBlank(overlay.getOverlayLabel())) {
					resourceName = overlay.getOverlayLabel();
				}
			}
			AuthRes authRes =
					AuthRes.of(resourceName, res.getId().toString(), res.getId(), parentId, displayOrder);
			authRes.setResourceCode(res.getResourceCode());
			authRes.setResourcePath(res.getResourcePath());
			authRes.setRequestMethod(res.getRequestMethod());
			authRes.setResourceType(res.getResourceType());
			authRes.setMenuNodeKind(res.getMenuNodeKind());
			roleRes.parallelStream().forEach(role -> {
				if (authRes.getId().equals(role.getId())) {
					authRes.setDisabled(role.isInherit());
				}
			});
			return authRes;
		}).collect(Collectors.toList());

		authResList = TreeUtils.build(authResList, Constants.MENU_ROOT_ID);

		RoleResTree roleResTree = new RoleResTree();
		roleResTree.setRoleRes(roleRes);
		roleResTree.setAuthRes(authResList);

		return roleResTree;
	}

	/**
	 * 授权保存合并（R13）：操作者不可见的既有直接授权项原样保留，最终落库 = 不可见既有项 ∪ 本次可见选择。
	 * 超管全可见（allowedIds=null）不触发合并。
	 */
	public List<String> mergePreservingInvisible(Long roleId, List<String> resIds, Set<Long> allowedIds) {
		if (allowedIds == null || roleId == null || resIds == null) {
			return resIds;
		}
		List<WoResource> existing = this.resourceService.queryResourceByRoleId(roleId);
		if (ObjectUtils.isBlank(existing)) {
			return resIds;
		}
		Set<String> merged = new LinkedHashSet<>(resIds);
		for (WoResource res : existing) {
			if (res.getId() != null && !allowedIds.contains(res.getId())) {
				merged.add(String.valueOf(res.getId()));
			}
		}
		return new ArrayList<>(merged);
	}

	/**
	 * 给角色授权：排除继承权限、删除自有权限、新增授权
	 *
	 * @param resIds 新权限
	 * @param roleId 角色
	 * @param curUserId 用户id
	 * @param uip 用户ip
	 */
	public void authRole(List<String> resIds, Long roleId, Long curUserId, String uip) {
		this.authRole(resIds, roleId, JurisdictionOverlayAxis.baseline(), curUserId, uip);
	}

	public void authRole(List<String> resIds, Long roleId, Long overlayDomainId, Long curUserId, String uip) {
		this.authRole(resIds, roleId, JurisdictionOverlayAxis.domain(overlayDomainId), curUserId, uip);
	}

	public void authRole(
			List<String> resIds,
			Long roleId,
			JurisdictionOverlayAxis axis,
			Long curUserId,
			String uip) {
		List<WoResource> authInherit = this.resourceService.queryResourceByInheritRoleId(roleId);
		JurisdictionOverlayAxis effective = axis == null ? JurisdictionOverlayAxis.baseline() : axis;
		WoRole roleRow = this.findById(roleId);
		effective = this.normalizeIdentityAuthAxis(roleRow, effective);
		Long overlayDomainId = effective.getDomainId();
		Long overlayComId = effective.getComId();
		// 由于角色配置主要集中在系统初始化阶段，并发不会很大，故此处统一处理
		List<WoAuthRole> authRoleList = (ObjectUtils.isBlank(authInherit) ?
				resIds.parallelStream().map(item -> {
					Long resId = Long.parseLong(item);

					WoAuthRole authRole = new WoAuthRole();
					authRole.setRoleId(roleId);
					authRole.setResourceId(resId);
					WoResource resource = this.resourceService.findById(resId);
					authRole.setAppId(resource.getAppId());
					authRole.setDomainId(overlayDomainId);
					authRole.setComId(overlayComId);

					return authRole;
				}) : resIds.parallelStream().map(item -> {
			Long resId = Long.parseLong(item);
			if (authInherit.parallelStream().anyMatch(auth -> resId.equals(auth.getId()))) // 1.过滤掉继承资源
				return null;

			WoAuthRole authRole = new WoAuthRole();
			authRole.setRoleId(roleId);
			authRole.setResourceId(resId);
			WoResource resource = this.resourceService.findById(resId);
			authRole.setAppId(resource.getAppId());
			authRole.setDomainId(overlayDomainId);
			authRole.setComId(overlayComId);

			return authRole;
		}).filter(Objects::nonNull)).collect(Collectors.toList());

		if (effective.isBaseline()) {
			this.authRoleRepo.deleteBaselineByRoleId(roleId);
			if (roleRow != null && GrantVisibilityPolicy.isIdentityRoleCode(roleRow.getRoleCode())) {
				this.authRoleRepo.deleteDomainOverlaysByRoleId(roleId);
			}
		}
		else if (overlayDomainId != null) {
			this.authRoleRepo.deleteOverlayByRoleIdAndDomainId(roleId, overlayDomainId);
		}
		else if (overlayComId != null) {
			this.authRoleRepo.deleteOverlayByRoleIdAndComId(roleId, overlayComId);
		}

		if (!authRoleList.isEmpty()) {
			this.authRoleRepo.saveAll(authRoleList);
		}
	}

	private JurisdictionOverlayAxis normalizeIdentityAuthAxis(WoRole roleRow, JurisdictionOverlayAxis axis) {
		if (roleRow == null || !GrantVisibilityPolicy.isIdentityRoleCode(roleRow.getRoleCode())) {
			return axis;
		}
		if (axis != null && axis.getDomainId() != null) {
			throw new BaseException("身份锚点角色禁止按站点维保存授权", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (Constants.TenantAdminRoleCode.equals(roleRow.getRoleCode())) {
			return axis == null ? JurisdictionOverlayAxis.baseline() : axis;
		}
		if (axis != null && axis.getComId() != null) {
			throw new BaseException("该身份锚点角色仅支持平台基线授权", ResultCode.VALIDATION_ERROR.getCode());
		}
		return JurisdictionOverlayAxis.baseline();
	}

	/**
	 * 查询资源属性
	 *
	 * @param resId 角色id
	 * @return 资源
	 */
	public WoResource findResById(Long resId) {
		return this.resourceService.findById(resId);
	}

	public List<WoRoleOrg> queryRoleByOrgId(Long orgId) {
		return this.roleOrgRepo.queryRoleByOrgId(orgId);
	}

	/**
	 * 查询租户角色（租户可指派的业务角色，来自系统应用创建）：{@code com_id=0} 的平台预置模板与
	 * {@code comId} 自建的角色，不含其它租户自建的同类角色。
	 *
	 * @param comId 调用方租户 id
	 * @return 租户角色列表
	 */
	public List<WoRole> findAllTenant(Long comId) {
		return this.entityRepo.findAllTenant(RoleTypeEnum.TAL_ROLE.toString(), comId);
	}

	public List<WoRole> findOrgAuthCandidates(Long comId) {
		return findOrgAuthCandidates(comId, null);
	}

	/**
	 * 组织授权候选：内置会员角色（本站实例）+ 本租户自定义/主体角色。
	 *
	 * @param comId 目标组织归属租户 id
	 * @param domainId 站点 id；非空时只取该站实例，不含平台 com_id=0 模板行
	 */
	public List<WoRole> findOrgAuthCandidates(Long comId, Long domainId) {
		Map<Long, WoRole> byId = new LinkedHashMap<>();
		for (WoRole role : this.entityRepo.findAllTenantForOrgAuth(RoleTypeEnum.TAL_ROLE.toString(), comId, domainId)) {
			byId.put(role.getId(), role);
		}
		for (WoRole role : this.entityRepo.findAllTenantForOrgAuth(RoleTypeEnum.SUBJECT.toString(), comId, domainId)) {
			byId.put(role.getId(), role);
		}
		List<WoRole> operational;
		if (domainId != null && domainId > 0 && comId != null) {
			operational = this.entityRepo.findOperationalByComIdAndDomainId(
					comId, domainId, GrantVisibilityPolicy.operationalRoleCodes());
		}
		else {
			operational = this.entityRepo.findAllOperationalByRoleCodes(GrantVisibilityPolicy.operationalRoleCodes());
		}
		if (operational != null) {
			Map<String, WoRole> operationalByCode = new LinkedHashMap<>();
			for (WoRole role : operational) {
				if (role != null && role.getRoleCode() != null
						&& GrantVisibilityPolicy.isOperationalRoleCode(role.getRoleCode())) {
					operationalByCode.putIfAbsent(role.getRoleCode(), role);
				}
			}
			for (WoRole role : operationalByCode.values()) {
				byId.put(role.getId(), role);
			}
		}
		return new ArrayList<>(byId.values());
	}
}
