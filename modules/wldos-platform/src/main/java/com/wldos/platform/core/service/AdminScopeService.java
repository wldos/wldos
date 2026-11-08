/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.NonEntityService;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.Constants;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;
import io.github.wldos.platform.support.rbac.AxisLayerGuard;
import io.github.wldos.platform.support.rbac.DataScope;
import io.github.wldos.platform.support.rbac.GrantAction;
import io.github.wldos.platform.support.rbac.GrantClass;
import io.github.wldos.platform.support.rbac.GrantVisibilityContext;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.ResourceScopeLevel;
import io.github.wldos.platform.support.rbac.ScopeDimensionCode;
import io.github.wldos.platform.support.resource.entity.WoResource;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import com.wldos.platform.core.dao.DomainAdminDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.RoleDimensionDao;
import com.wldos.platform.core.dao.ScopeDimensionDao;
import com.wldos.platform.core.entity.WoDomainAdmin;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoOrgUser;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoRoleDimension;
import com.wldos.platform.core.entity.WoScopeDimension;
import com.wldos.platform.core.enums.RoleTypeEnum;
import com.wldos.platform.core.support.ScopeDimensionCatalogHelper;
import com.wldos.platform.core.vo.ScopeDictSourceVO;
import com.wldos.platform.core.vo.RoleScopeDimensionsVO;
import com.wldos.platform.core.vo.ScopeExtensionDimVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 管理员授权天花板：解析当前操作者层级，校验可授出的资源与数据策略。
 * 
 * @author Yuanxi Universe (306991142@qq.com)
 * @since 3.0.1
 * @version 1.0
 */
@Service
public class AdminScopeService extends NonEntityService {

	@Autowired
	@Lazy
	private ResourceService resourceService;

	@Autowired
	@Lazy
	private RoleDao roleDao;

	@Autowired
	@Lazy
	private ScopeDimensionDao scopeDimensionDao;

	@Autowired
	@Lazy
	private RoleDimensionDao roleDimensionDao;

	@Autowired
	@Lazy
	private ScopeDictSourcesService scopeDictSourcesService;

	@Autowired
	@Lazy
	private DomainAdminDao domainAdminDao;

	@Autowired
	@Lazy
	private OrgDao orgDao;

	@Autowired
	@Lazy
	private OrgUserDao orgUserDao;

	@Autowired
	@Lazy
	private GrantVisibilityService grantVisibilityService;

	public RoleScopeDimensionsVO grantableScopeDimensionsView(Long grantorId, Long roleId) {
		RoleScopeDimensionsVO view = new RoleScopeDimensionsVO();
		if (!canQueryScopeDimensions(grantorId)) {
			return view;
		}
		List<WoScopeDimension> catalog = this.scopeDimensionDao.findAllFilterCatalog();
		view.setGlobalDimensions(resolveGlobalDimensions(catalog));
		if (roleId == null) {
			return view;
		}
		Set<String> globalDictCodes = resolveGlobalDictCodes(catalog);
		Set<String> inheritedDimCodes = resolveInheritedAdoptedCodes(roleId);
		Set<String> effectiveDimCodes = resolveEffectiveAdoptedCodes(roleId);
		Map<String, WoScopeDimension> catalogByCode = catalog.stream()
				.collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));
		List<ScopeExtensionDimVO> extension = new ArrayList<>();
		for (String dimCode : effectiveDimCodes) {
			WoScopeDimension row = catalogByCode.get(dimCode);
			if (row == null || !ScopeDimensionCatalogHelper.isDictDimension(row)) {
				continue;
			}
			String dictCode = ScopeDictSourcesService.extractDictCode(row.getValueSource());
			if (dictCode == null || globalDictCodes.contains(dictCode)) {
				continue;
			}
			ScopeExtensionDimVO item = new ScopeExtensionDimVO();
			item.setDictCode(dictCode);
			item.setDictName(this.scopeDictSourcesService.resolveDictName(dictCode));
			item.setDimensionCode(dimCode);
			item.setInherited(inheritedDimCodes.contains(dimCode));
			extension.add(item);
		}
		view.setCustomDimensions(extension);
		return view;
	}

	public List<WoScopeDimension> grantableScopeDimensions(Long grantorId) {
		return grantableScopeDimensions(grantorId, null);
	}

	public List<WoScopeDimension> grantableScopeDimensions(Long grantorId, Long roleId) {
		RoleScopeDimensionsVO view = grantableScopeDimensionsView(grantorId, roleId);
		List<WoScopeDimension> merged = new ArrayList<>(view.getGlobalDimensions());
		if (view.getCustomDimensions() != null) {
			Map<String, WoScopeDimension> catalogByCode = this.scopeDimensionDao.findAllFilterCatalog().stream()
					.collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));
			for (ScopeExtensionDimVO ext : view.getCustomDimensions()) {
				WoScopeDimension row = catalogByCode.get(ext.getDimensionCode());
				if (row != null) {
					merged.add(row);
				}
			}
		}
		return merged;
	}

	public List<ScopeDictSourceVO> pickableDictTypes(Long grantorId, Long roleId) {
		if (!canQueryScopeDimensions(grantorId)) {
			return Collections.emptyList();
		}
		Set<String> excluded = new LinkedHashSet<>();
		List<WoScopeDimension> catalog = this.scopeDimensionDao.findAllFilterCatalog();
		excluded.addAll(resolveGlobalDictCodes(catalog));
		for (WoScopeDimension row : this.scopeDimensionDao.findAllDictDimensions()) {
			if (!"1".equals(row.getEnabled())) {
				String dictCode = ScopeDictSourcesService.extractDictCode(row.getValueSource());
				if (dictCode != null) {
					excluded.add(dictCode);
				}
			}
		}
		if (roleId != null) {
			excluded.addAll(resolveEffectiveAdoptedDictCodes(roleId, catalog));
		}
		return this.scopeDictSourcesService.listDictSources().stream()
				.filter(item -> item.getDictCode() != null && !excluded.contains(item.getDictCode()))
				.collect(Collectors.toList());
	}

	public List<WoScopeDimension> pickableScopeDimensions(Long grantorId, Long roleId) {
		return Collections.emptyList();
	}

	public Set<String> resolveGlobalDictCodes(List<WoScopeDimension> catalog) {
		if (catalog == null || catalog.isEmpty()) {
			return Collections.emptySet();
		}
		Set<String> codes = new LinkedHashSet<>();
		for (WoScopeDimension row : catalog) {
			if (!ScopeDimensionCatalogHelper.isGlobalDimension(row)
					|| !ScopeDimensionCatalogHelper.isDictDimension(row)) {
				continue;
			}
			String dictCode = ScopeDictSourcesService.extractDictCode(row.getValueSource());
			if (dictCode != null) {
				codes.add(dictCode);
			}
		}
		return codes;
	}

	public Set<String> resolveEffectiveAdoptedDictCodes(Long roleId, List<WoScopeDimension> catalog) {
		if (roleId == null) {
			return Collections.emptySet();
		}
		Map<String, WoScopeDimension> catalogByCode = catalog == null ? Collections.emptyMap()
				: catalog.stream().collect(Collectors.toMap(WoScopeDimension::getDimensionCode, d -> d, (a, b) -> a));
		Set<String> dictCodes = new LinkedHashSet<>();
		for (String dimCode : resolveEffectiveAdoptedCodes(roleId)) {
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

	private List<WoScopeDimension> resolveGlobalDimensions(List<WoScopeDimension> catalog) {
		return catalog.stream()
				.filter(ScopeDimensionCatalogHelper::isGlobalDimension)
				.sorted(this::compareBySortOrder)
				.collect(Collectors.toList());
	}

	public Set<String> resolveEffectiveAdoptedCodes(Long roleId) {
		if (roleId == null) {
			return Collections.emptySet();
		}
		Set<String> codes = new LinkedHashSet<>();
		WoRole current = this.roleDao.findById(roleId).orElse(null);
		while (current != null) {
			List<WoRoleDimension> rows = this.roleDimensionDao.findByRoleId(current.getId());
			if (rows != null) {
				for (WoRoleDimension row : rows) {
					if (row.getDimensionCode() != null) {
						codes.add(row.getDimensionCode());
					}
				}
			}
			Long parentId = current.getParentId();
			if (parentId == null || parentId <= 0) {
				break;
			}
			current = this.roleDao.findById(parentId).orElse(null);
		}
		return codes;
	}

	public Set<String> resolveInheritedAdoptedCodes(Long roleId) {
		if (roleId == null) {
			return Collections.emptySet();
		}
		WoRole role = this.roleDao.findById(roleId).orElse(null);
		if (role == null || role.getParentId() == null || role.getParentId() <= 0) {
			return Collections.emptySet();
		}
		return resolveEffectiveAdoptedCodes(role.getParentId());
	}

	private boolean canQueryScopeDimensions(Long grantorId) {
		if (grantorId == null || this.commonOperate.isAdmin(grantorId)) {
			return true;
		}
		AdminScopeLevel level = resolveLevel(grantorId);
		return level != AdminScopeLevel.MEMBER && level != AdminScopeLevel.ORG;
	}

	private int compareBySortOrder(WoScopeDimension a, WoScopeDimension b) {
		int left = a.getSortOrder() != null ? a.getSortOrder() : 0;
		int right = b.getSortOrder() != null ? b.getSortOrder() : 0;
		if (left != right) {
			return Integer.compare(left, right);
		}
		return Long.compare(
				a.getId() != null ? a.getId() : 0L,
				b.getId() != null ? b.getId() : 0L);
	}

	private boolean canGrantScopeDimension(AdminScopeLevel grantorLevel, String dimensionCode) {
		if (dimensionCode == null || grantorLevel == null) {
			return false;
		}
		if (ScopeDimensionCode.isOperatorDimension(dimensionCode)) {
			return false;
		}
		if (grantorLevel == AdminScopeLevel.PLATFORM) {
			return true;
		}
		return grantorLevel == AdminScopeLevel.TENANT || grantorLevel == AdminScopeLevel.DOMAIN;
	}

	public AdminScopeLevel resolveLevel(Long userId) {
		if (userId == null) {
			return AdminScopeLevel.MEMBER;
		}
		if (this.commonOperate.isAdmin(userId)) {
			return AdminScopeLevel.PLATFORM;
		}
		if (this.commonOperate.isTenantAdmin(userId)) {
			return AdminScopeLevel.TENANT;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			return AdminScopeLevel.DOMAIN;
		}
		if (this.commonOperate.getPrimaryOrgId(userId) != null) {
			return AdminScopeLevel.ORG;
		}
		return AdminScopeLevel.MEMBER;
	}

	/** 用户身份层级（平台超管/租户管/站点管，不含普通组织成员推导）。 */
	public AdminScopeLevel resolveIdentityLevel(Long userId) {
		if (userId == null) {
			return AdminScopeLevel.MEMBER;
		}
		if (this.commonOperate.isAdmin(userId)) {
			return AdminScopeLevel.PLATFORM;
		}
		if (this.commonOperate.isTenantAdmin(userId)) {
			return AdminScopeLevel.TENANT;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			return AdminScopeLevel.DOMAIN;
		}
		return AdminScopeLevel.MEMBER;
	}

	/**
	 * 批量解析用户身份层级，供列表页 writable 计算。
	 */
	public Map<Long, AdminScopeLevel> resolveIdentityLevels(Collection<Long> userIds) {
		Map<Long, AdminScopeLevel> levels = new HashMap<>();
		if (userIds == null || userIds.isEmpty()) {
			return levels;
		}
		Set<Long> uniq = new HashSet<>(userIds);
		Set<Long> platform = new HashSet<>(this.commonOperate.listSuperAdmin());
		Set<Long> tenant = new HashSet<>(this.commonOperate.listTenantAdmin());
		Set<Long> domain = new HashSet<>();
		List<WoDomainAdmin> bindings = this.domainAdminDao.findByUserIds(uniq);
		if (bindings != null) {
			for (WoDomainAdmin row : bindings) {
				if (row.getUserId() != null) {
					domain.add(row.getUserId());
				}
			}
		}
		WoOrg dadminOrg = this.orgDao.findByOrgCodeAndIsValidAndDeleteFlag(Constants.DomainAdminOrgCode,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (dadminOrg != null) {
			List<WoOrgUser> members = this.orgUserDao.queryAllByUserIds(dadminOrg.getId(), new ArrayList<>(uniq),
					ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
			if (members != null) {
				for (WoOrgUser member : members) {
					if (member.getUserId() != null) {
						domain.add(member.getUserId());
					}
				}
			}
		}
		for (Long uid : uniq) {
			if (platform.contains(uid)) {
				levels.put(uid, AdminScopeLevel.PLATFORM);
			}
			else if (tenant.contains(uid)) {
				levels.put(uid, AdminScopeLevel.TENANT);
			}
			else if (domain.contains(uid)) {
				levels.put(uid, AdminScopeLevel.DOMAIN);
			}
			else {
				levels.put(uid, AdminScopeLevel.MEMBER);
			}
		}
		return levels;
	}

	/**
	 * 用户写操作的身份层级校验：仅禁止操作身份严格高于操作者的账号；平级与下级允许（末位管理员删除由业务 guard 兜底）。
	 * 超管短路；操作者操作自己放行（不构成提权）。
	 */
	public void assertTargetIdentityLowerThanOperator(Long operatorId, Long targetUserId) {
		if (operatorId == null || targetUserId == null) {
			return;
		}
		if (this.commonOperate.isAdmin(operatorId) || operatorId.equals(targetUserId)) {
			return;
		}
		AdminScopeLevel operator = resolveLevel(operatorId);
		AdminScopeLevel target = resolveIdentityLevel(targetUserId);
		if (operator.getRank() < target.getRank()) {
			throw new BaseException("无权操作该身份层级的用户", ResultCode.FORBIDDEN.getCode());
		}
	}

	public boolean canGrantResource(Long grantorId, WoResource resource) {
		if (grantorId == null || resource == null) {
			return false;
		}
		if (this.commonOperate.isAdmin(grantorId)) {
			return true;
		}
		AdminScopeLevel grantor = resolveLevel(grantorId);
		ResourceScopeLevel resLevel = ResourceScopeLevel.ofCode(resource.getScopeLevel());
		return grantor.canGrant(resLevel.toAdminScopeLevel());
	}

	public boolean canGrantDataPolicy(Long grantorId, DataScope target) {
		if (grantorId == null || target == null) {
			return false;
		}
		if (this.commonOperate.isAdmin(grantorId)) {
			return true;
		}
		AdminScopeLevel grantor = resolveLevel(grantorId);
		switch (grantor) {
			case PLATFORM:
				return true;
			case TENANT:
				return target != DataScope.ALL;
			case DOMAIN:
				return target == DataScope.DOMAIN || target == DataScope.TENANT_DOMAIN
						|| target == DataScope.ORG || target == DataScope.ORG_SUBTREE
						|| target == DataScope.SELF;
			case ORG:
				return target == DataScope.ORG || target == DataScope.ORG_SUBTREE || target == DataScope.SELF;
			default:
				return target == DataScope.SELF;
		}
	}

	public Set<Long> grantableResourceIds(Long domainId, Long comId, Long grantorId) {
		return resolveGrantorEffectiveResourceIds(domainId, comId, grantorId);
	}

	public Set<Long> resolveGrantorEffectiveResourceIds(Long domainId, Long comId, Long grantorId) {
		if (this.commonOperate.isAdmin(grantorId)) {
			return resourceService.listGrantCatalogResources().stream().map(WoResource::getId).collect(Collectors.toSet());
		}
		Set<Long> ids = new HashSet<>();
		for (String type : new String[] {
				ResourceEnum.ADMIN_MENU.getValue(),
				ResourceEnum.ADMIN_PLUGIN_MENU.getValue(),
				ResourceEnum.ADMIN_BUTTON.getValue(),
				ResourceEnum.ADMIN_PLUGIN_BUTTON.getValue(),
				ResourceEnum.MENU.getValue(),
				ResourceEnum.PLUGIN_MENU.getValue(),
				ResourceEnum.BUTTON.getValue(),
				ResourceEnum.PLUGIN_BUTTON.getValue()
		}) {
			List<WoResource> batch = resourceService.queryResource(domainId, comId, type, grantorId);
			if (batch != null) {
				for (WoResource res : batch) {
					ids.add(res.getId());
				}
			}
		}
		return ids;
	}

	/**
	 * 授权保存校验：仅校验**新增**资源 id 是否在操作者可授集合内；已有直接授权允许收回（含上级曾授予项）。
	 */
	public void assertCanAuthResources(Long domainId, Long comId, Long grantorId, Long roleId, List<String> resIds) {
		if (this.commonOperate.isAdmin(grantorId)) {
			return;
		}
		if (resIds == null || resIds.isEmpty()) {
			return;
		}
		Set<Long> allowed = resolveGrantorEffectiveResourceIds(domainId, comId, grantorId);
		Set<Long> existing = new HashSet<>();
		if (roleId != null) {
			List<WoResource> direct = this.resourceService.queryResourceByRoleId(roleId);
			if (direct != null) {
				for (WoResource res : direct) {
					if (res.getId() != null) {
						existing.add(res.getId());
					}
				}
			}
		}
		for (String raw : resIds) {
			Long resId = Long.parseLong(raw);
			if (existing.contains(resId)) {
				continue;
			}
			if (!allowed.contains(resId)) {
				throw new BaseException("无权授予资源 id=" + resId, ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	public AdminScopeLevel resolveRoleLevel(WoRole role) {
		if (role == null) {
			return AdminScopeLevel.MEMBER;
		}
		if (Constants.isPlatformRootRoleCode(role.getRoleCode())) {
			return AdminScopeLevel.PLATFORM;
		}
		// 行归属轴（comId/domainId 是否为空/0）决定这行"写在哪一层"，是比 data_policy 更硬的下限：
		// 一个 domain 为空的租户共享角色，即便 data_policy 只授 DOMAIN，也不允许被站点管理员改写。
		AdminScopeLevel axisLevel = AxisLayerGuard.resolveAxisLayer(role.getComId(), role.getDomainId());
		return AxisLayerGuard.stricterOf(axisLevel, resolvePolicyLevel(role));
	}

	private AdminScopeLevel resolvePolicyLevel(WoRole role) {
		DataScope scope = DataScope.ofCode(role.getDataPolicy());
		if (scope == null) {
			return AdminScopeLevel.ORG;
		}
		switch (scope) {
			case ALL:
				return AdminScopeLevel.PLATFORM;
			case TENANT:
			case TENANT_DOMAIN:
				return AdminScopeLevel.TENANT;
			case DOMAIN:
				return AdminScopeLevel.DOMAIN;
			case ORG:
			case ORG_SUBTREE:
				return AdminScopeLevel.ORG;
			case SELF:
			default:
				return AdminScopeLevel.MEMBER;
		}
	}

	public boolean canAssignRole(Long grantorId, WoRole target) {
		if (grantorId == null || target == null) {
			return false;
		}
		if (this.commonOperate.isAdmin(grantorId)) {
			return true;
		}
		GrantClass cls = GrantVisibilityPolicy.classifyRole(
				target.getRoleCode(), target.getRoleType(), target.getComId(), target.getDomainId(), target.isSystemReserved());
		GrantVisibilityContext ctx = buildGrantContext(grantorId);
		if (!GrantVisibilityPolicy.allowed(ctx, cls, GrantAction.ASSIGN, target.getComId(), target.getDomainId())) {
			return false;
		}
		if (cls == GrantClass.OPERATIONAL) {
			return true;
		}
		if (RoleTypeEnum.TAL_ROLE.toString().equals(target.getRoleType())) {
			return true;
		}
		AdminScopeLevel grantor = resolveLevel(grantorId);
		AdminScopeLevel targetLevel = resolveRoleAssignLevel(target);
		return grantor.canGrant(targetLevel);
	}

	public boolean canManageRole(Long grantorId, WoRole target) {
		if (grantorId == null || target == null) {
			return false;
		}
		if (this.commonOperate.isAdmin(grantorId)) {
			return true;
		}
		GrantClass cls = GrantVisibilityPolicy.classifyRole(
				target.getRoleCode(), target.getRoleType(), target.getComId(), target.getDomainId(), target.isSystemReserved());
		GrantVisibilityContext ctx = buildGrantContext(grantorId);
		if (!GrantVisibilityPolicy.allowed(ctx, cls, GrantAction.MANAGE, target.getComId(), target.getDomainId())) {
			return false;
		}
		if (cls == GrantClass.OPERATIONAL || cls == GrantClass.IDENTITY) {
			return false;
		}
		AdminScopeLevel grantor = resolveLevel(grantorId);
		AdminScopeLevel targetLevel = resolveRoleLevel(target);
		return grantor.canGrant(targetLevel);
	}

	public void assertCanManageRole(Long grantorId, WoRole target) {
		if (!canManageRole(grantorId, target)) {
			throw new BaseException("无权管理该角色", ResultCode.FORBIDDEN.getCode());
		}
	}

	/**
	 * 挂接/可见层级：租户可复用角色模板（{@code role_type=tal_role}，如 trole/tauthor）不套用归属轴
	 * 的 PLATFORM 下限，下放到 TENANT 级——任意租户/站点/组织级操作者都能在角色列表中看到、并勾选挂接
	 * 到自己的组织；真正的平台身份锚点角色仍按 {@link #resolveRoleLevel} 锁在 PLATFORM。
	 * 共享 = 可见 + 可用，不等于可改：改写该角色定义本身仍须走 {@link #resolveRoleLevel}/{@link #canManageRole}。
	 */
	public AdminScopeLevel resolveRoleAssignLevel(WoRole role) {
		if (role == null) {
			return AdminScopeLevel.MEMBER;
		}
		if (Constants.isPlatformRootRoleCode(role.getRoleCode())) {
			return AdminScopeLevel.PLATFORM;
		}
		if (RoleTypeEnum.TAL_ROLE.toString().equals(role.getRoleType())) {
			return AxisLayerGuard.stricterOf(AdminScopeLevel.TENANT, resolvePolicyLevel(role));
		}
		return resolveRoleLevel(role);
	}

	public void assertCanAssignRole(Long grantorId, WoRole target) {
		if (!canAssignRole(grantorId, target)) {
			throw new BaseException("无权配置该角色", ResultCode.FORBIDDEN.getCode());
		}
	}

	/**
	 * 与 {@link GrantVisibilityService#contextFor} 共用同一实现，避免列表与提交校验结果不一致。
	 */
	private GrantVisibilityContext buildGrantContext(Long grantorId) {
		return this.grantVisibilityService.contextFor(grantorId);
	}
}
