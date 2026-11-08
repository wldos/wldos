/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import com.wldos.platform.core.enums.OrgTypeEnum;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;
import io.github.wldos.platform.support.rbac.DataScopeGuard;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import io.github.wldos.platform.support.rbac.GrantAction;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;
import io.github.wldos.platform.support.rbac.kind.SystemOrgKind;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.vo.OrgMemberAddBody;
import com.wldos.platform.core.service.AdminScopeService;
import com.wldos.platform.core.service.CompanyService;
import com.wldos.platform.core.service.DomainService;
import com.wldos.platform.core.service.GrantVisibilityService;
import com.wldos.platform.core.service.IdentityRegistryService;
import com.wldos.platform.core.service.OrgService;
import com.wldos.platform.core.service.OrgClosureService;
import com.wldos.platform.core.service.OperationalTreeDeduper;
import com.wldos.platform.core.service.OrgOperationalKindBridge;
import com.wldos.platform.core.service.OrgReferenceGuard;
import com.wldos.platform.core.service.PlatformOperatorSettingsService;
import com.wldos.platform.core.service.RoleOperationalKindBridge;
import com.wldos.platform.core.service.RoleService;
import com.wldos.platform.core.vo.Org;
import com.wldos.platform.core.vo.OrgRoleTree;

import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 公司相关controller。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "组织管理")
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/org")
public class OrgController extends EntityController<OrgService, WoOrg> {

	private final RoleService roleService;

	private final OrgClosureService orgClosureService;

	private final AdminScopeService adminScopeService;

	private final GrantVisibilityService grantVisibilityService;

	private final RoleOperationalKindBridge roleOperationalKindBridge;

	private final OrgOperationalKindBridge orgOperationalKindBridge;

	private final CompanyService companyService;

	private final DomainService domainService;

	private final IdentityRegistryService identityRegistryService;

	private final OrgReferenceGuard orgReferenceGuard;

	private final PlatformOperatorSettingsService platformOperatorSettingsService;

	private Long pendingOldParentId;

	public OrgController(
			RoleService roleService,
			OrgClosureService orgClosureService,
			AdminScopeService adminScopeService,
			GrantVisibilityService grantVisibilityService,
			RoleOperationalKindBridge roleOperationalKindBridge,
			OrgOperationalKindBridge orgOperationalKindBridge,
			CompanyService companyService,
			@Lazy DomainService domainService,
			IdentityRegistryService identityRegistryService,
			OrgReferenceGuard orgReferenceGuard,
			@Lazy PlatformOperatorSettingsService platformOperatorSettingsService) {
		this.roleService = roleService;
		this.orgClosureService = orgClosureService;
		this.adminScopeService = adminScopeService;
		this.grantVisibilityService = grantVisibilityService;
		this.roleOperationalKindBridge = roleOperationalKindBridge;
		this.orgOperationalKindBridge = orgOperationalKindBridge;
		this.companyService = companyService;
		this.domainService = domainService;
		this.identityRegistryService = identityRegistryService;
		this.orgReferenceGuard = orgReferenceGuard;
		this.platformOperatorSettingsService = platformOperatorSettingsService;
	}

	@ApiOperation(value = "组织列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "orgName", value = "组织名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（站点域筛选）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管跨宇宙视图）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<Org> listQuery(@RequestParam Map<String, Object> params) {
		// 组织页分区：staff=编制树；segment=会员分群；缺省为全量（兼容旧调用）
		String category = ObjectUtils.string(params.remove("category"));
		PageQuery pageQuery = new PageQuery(params);
		Long userId = this.getUserId();
		Long scopedTargetComId = this.grantVisibilityService.parseScopedTargetComId(params);
		if (this.commonOperate.isDomainAdmin(userId) && !this.commonOperate.isTenantAdmin(userId)) {
			this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, WoOrg.class);
		} else {
			this.applyScope(pageQuery, DataScopeProfile.ADMIN_ORG_TREE, WoOrg.class);
		}
		Long targetDomainId = this.roleOperationalKindBridge.parseTargetDomainId(
				params, this.getDomainId(), this.commonOperate.listBoundDomainIds(userId));
		if (this.commonOperate.isAdmin(userId)) {
			Long tenantForDomain = null;
			if (targetDomainId != null && targetDomainId > 0
					&& (scopedTargetComId == null || scopedTargetComId <= 0)) {
				tenantForDomain = this.orgOperationalKindBridge.resolveComIdForDomain(
						targetDomainId, this.getTenantId());
			}
			DataScopeQueryHelper.applyAdminOperationalSharedRead(
					pageQuery, scopedTargetComId, targetDomainId, tenantForDomain);
		} else if (targetDomainId != null && targetDomainId > 0) {
			pageQuery.getCondition().remove("domainId");
			pageQuery.removeParam(Constants.COMMON_KEY_DOMAIN_COLUMN);
			Long comId = this.orgOperationalKindBridge.resolveComIdForDomain(targetDomainId, this.getTenantId());
			DataScopeQueryHelper.applySharedDomainAdminRead(
					pageQuery,
					comId,
					java.util.Collections.singletonList(targetDomainId));
		}

		PageData<Org> page = this.service.execQueryForTree(new Org(), new WoOrg(), pageQuery, Constants.TOP_ORG_ID);
		if (targetDomainId != null && targetDomainId > 0) {
			this.reconcileOperationalOrgInstances(page.getRows(), targetDomainId);
			OperationalTreeDeduper.dedupeOrgTree(page.getRows(), targetDomainId);
		}
		this.grantVisibilityService.filterOrgTree(userId, page.getRows());
		if (this.commonOperate.isAdmin(userId)) {
			if (this.shouldStripPlatformIdentityOrgsForScopedCom(scopedTargetComId)) {
				this.grantVisibilityService.stripPlatformIdentityOrgTree(page.getRows(), scopedTargetComId);
			}
			if (scopedTargetComId != null) {
				this.grantVisibilityService.filterOrgTreeByScopedComId(page.getRows(), scopedTargetComId);
			}
		}
		this.decorateOrgList(page.getRows());
		this.applyCategoryFilter(page.getRows(), category);
		return page;
	}

	private boolean shouldStripPlatformIdentityOrgsForScopedCom(Long scopedComId) {
		if (scopedComId == null || scopedComId <= 0) {
			return false;
		}
		return !this.platformOperatorSettingsService.isPlatformOperatorCom(scopedComId);
	}

	/** 分区剪枝：staff 剔除身份组/分群；segment 仅保留会员分群子树。 */
	private void applyCategoryFilter(List<Org> rows, String category) {
		if (rows == null || ObjectUtils.isBlank(category)) {
			return;
		}
		if ("staff".equals(category)) {
			rows.removeIf(o -> o != null && (Constants.ENUM_TYPE_ORG_SEGMENT.equals(o.getOrgType())
					|| (o.getOrgCode() != null
							&& (GrantVisibilityPolicy.isIdentityOrgCode(o.getOrgCode())
									|| GrantVisibilityPolicy.isOperationalOrgCode(o.getOrgCode())))));
		}
		else if ("segment".equals(category)) {
			rows.removeIf(o -> o == null
					|| !(GrantVisibilityPolicy.isOperationalOrgCode(o.getOrgCode())
							|| Constants.ENUM_TYPE_ORG_SEGMENT.equals(o.getOrgType())));
		}
		else {
			return;
		}
		for (Org org : rows) {
			if (org != null) {
				this.applyCategoryFilter(org.getChildren(), category);
			}
		}
	}

	private void reconcileOperationalOrgInstances(List<Org> orgs, Long targetDomainId) {
		if (orgs == null || targetDomainId == null) {
			return;
		}
		Long comId = this.orgOperationalKindBridge.resolveComIdForDomain(targetDomainId, this.getTenantId());
		Iterator<Org> iterator = orgs.iterator();
		while (iterator.hasNext()) {
			Org org = iterator.next();
			if (org == null) {
				iterator.remove();
				continue;
			}
			if (org.getChildren() != null && !org.getChildren().isEmpty()) {
				this.reconcileOperationalOrgInstances(org.getChildren(), targetDomainId);
			}
			if (org.getOrgCode() == null || !GrantVisibilityPolicy.isOperationalOrgCode(org.getOrgCode())) {
				continue;
			}
			if (org.getDomainId() != null && org.getDomainId() > 0) {
				continue;
			}
			WoOrg wo = new WoOrg();
			wo.setId(org.getId());
			wo.setOrgCode(org.getOrgCode());
			wo.setOrgType(org.getOrgType());
			wo.setComId(org.getComId());
			wo.setDomainId(org.getDomainId());
			WoOrg instance = this.orgOperationalKindBridge.tryResolveOperationalInstance(wo, targetDomainId, comId);
			if (instance == null) {
				iterator.remove();
				continue;
			}
			org.setId(instance.getId());
			org.setDomainId(instance.getDomainId());
			org.setComId(instance.getComId());
		}
		orgs.removeIf(o -> o != null && GrantVisibilityPolicy.isOperationalOrgCode(o.getOrgCode())
				&& o.getDomainId() != null && o.getDomainId() > 0 && !targetDomainId.equals(o.getDomainId()));
	}

	private void decorateOrgList(List<Org> orgs) {
		if (orgs == null) {
			return;
		}
		for (Org org : orgs) {
			org.setSystemBuiltIn(SystemOrgKind.ofOrgCode(org.getOrgCode()) != null
					&& org.getDomainId() != null && org.getDomainId() > 0);
			org.setKindTemplate(SystemOrgKind.ofOrgCode(org.getOrgCode()) != null
					&& org.getComId() != null && org.getComId() == 0L
					&& (org.getDomainId() == null || org.getDomainId() <= 0));
			org.setDeletable(!GrantVisibilityPolicy.isUndeletableSystemGrantOrg(
					org.getOrgCode(), org.getOrgType(), org.getComId(), org.getDomainId(), false));
			if (org.getChildren() != null) {
				this.decorateOrgList(org.getChildren());
			}
		}
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoOrg org = this.service.findById(id);
		if (org != null) {
			this.assertEntityRowScope(org);
			this.grantVisibilityService.assertOrgAllowed(this.getUserId(), org, GrantAction.LIST);
		}
		return org;
	}

	@Override
	protected void preUpdate(WoOrg entity) {
		if (entity != null && entity.getId() != null) {
			WoOrg existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.pendingOldParentId = existing.getParentId();
				this.assertOrgTypeWritable(entity, existing);
				if (Constants.ENUM_TYPE_ORG_SEGMENT.equals(existing.getOrgType())) {
					entity.setParentId(existing.getParentId());
				}
				this.assertEntityRowScope(existing);
				this.grantVisibilityService.assertOrgAllowed(this.getUserId(), existing, GrantAction.MANAGE);
				this.assertSystemOrgMutable(existing, entity);
				if (!this.isAdmin(this.getUserId()) && this.isMultiTenancy()) {
					Long existingComId = existing.getComId();
					boolean existingComValid = existingComId != null && existingComId > 0
							&& !Objects.equals(existingComId, Constants.TOP_COM_ID);
					if (existingComValid) {
						DataScopeGuard.assertComIdImmutable(existingComId, entity.getComId());
					}
				}
				this.ensureOperationalOrgComId(entity);
				this.applyDefaultArchId(entity);
			}
		}
	}

	@Override
	protected void preDelete(WoOrg entity) {
		if (entity != null && entity.getId() != null) {
			WoOrg existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.grantVisibilityService.assertOrgAllowed(this.getUserId(), existing, GrantAction.MANAGE);
				this.assertSystemOrgDeletable(existing);
				this.orgReferenceGuard.assertDeletable(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			WoOrg existing = this.service.findById(Long.parseLong(raw.toString()));
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.grantVisibilityService.assertOrgAllowed(this.getUserId(), existing, GrantAction.MANAGE);
				this.assertSystemOrgDeletable(existing);
				this.orgReferenceGuard.assertDeletable(existing);
			}
		}
	}

	private void assertSystemOrgMutable(WoOrg existing, WoOrg incoming) {
		if (!existing.isSystemReserved()) {
			return;
		}
		if (!Constants.AdminOrgCode.equals(existing.getOrgCode()) && this.isAdmin(this.getUserId())) {
			incoming.setIsSystem(existing.getIsSystem());
			return;
		}
		if (incoming.getOrgCode() != null && !incoming.getOrgCode().equals(existing.getOrgCode())) {
			throw new BaseException("系统保留组织禁止修改标识（org_code）");
		}
		incoming.setIsSystem(existing.getIsSystem());
	}

	private void assertSystemOrgDeletable(WoOrg existing) {
		if (existing == null) {
			return;
		}
		if (GrantVisibilityPolicy.isUndeletableSystemGrantOrg(
				existing.getOrgCode(), existing.getOrgType(),
				existing.getComId(), existing.getDomainId(), existing.isSystemReserved())) {
			throw new BaseException("系统内置组织禁止删除");
		}
		if (existing.isSystemReserved()) {
			throw new BaseException("系统保留组织禁止删除");
		}
	}

	@Override
	protected void preAdd(WoOrg entity) {
		this.assertOrgTypeWritable(entity, null);
		// 分群为扁平成员集合，不挂编制树
		if (Constants.ENUM_TYPE_ORG_SEGMENT.equals(entity.getOrgType())) {
			entity.setParentId(Constants.TOP_ORG_ID);
		}
		Long userId = this.getUserId();
		if (this.isAdmin(userId)) {
			this.applyWriteScope(entity, WriteScopeTarget.TENANT_AXIS);
			this.ensureOperationalOrgComId(entity);
			this.applyDefaultArchId(entity);
			this.assertParentOrgComId(entity);
			return;
		}
		AdminScopeLevel level = this.adminScopeService.resolveLevel(userId);
		if (level == AdminScopeLevel.DOMAIN) {
			this.applyWriteScope(entity, WriteScopeTarget.SITE_AXIS);
			if (entity.getDomainId() == null || entity.getDomainId() <= 0) {
				throw new BaseException("未绑定站点，无法创建组织", ResultCode.VALIDATION_ERROR.getCode());
			}
		} else {
			this.applyWriteScope(entity, WriteScopeTarget.TENANT_AXIS);
		}
		this.ensureOperationalOrgComId(entity);
		this.applyDefaultArchId(entity);
		this.assertParentOrgComId(entity);
	}

	private void ensureOperationalOrgComId(WoOrg entity) {
		if (entity == null) {
			return;
		}
		Long comId = entity.getComId();
		if (comId != null && comId > 0 && !Objects.equals(comId, Constants.TOP_COM_ID)) {
			return;
		}
		Long domainId = entity.getDomainId();
		if (domainId != null && domainId > 0) {
			WoDomain domain = this.domainService.findById(domainId);
			if (domain != null && domain.getComId() != null && domain.getComId() > 0
					&& !Objects.equals(domain.getComId(), Constants.TOP_COM_ID)) {
				entity.setComId(domain.getComId());
				return;
			}
		}
		Long tenantId = this.getTenantId();
		if (tenantId != null && tenantId > 0) {
			entity.setComId(tenantId);
			return;
		}
		Long operatorComId = this.commonOperate.getOperatorComId(this.getUserId());
		if (operatorComId != null && operatorComId > 0 && !Objects.equals(operatorComId, Constants.TOP_COM_ID)) {
			entity.setComId(operatorComId);
			return;
		}
		throw new BaseException("无法确定组织归属租户", ResultCode.VALIDATION_ERROR.getCode());
	}

	/** 表单隐藏「归属体系」后的服务端默认填充（R35）：空值时落归属租户的唯一/首个体系，平台上下文落 0。 */
	private void applyDefaultArchId(WoOrg entity) {
		if (entity != null && entity.getArchId() == null) {
			entity.setArchId(this.service.resolveDefaultArchId(entity.getComId()));
		}
	}

	private void assertParentOrgComId(WoOrg entity) {
		if (entity == null) {
			return;
		}
		Long parentId = entity.getParentId();
		if (parentId == null || parentId <= 0) {
			return;
		}
		WoOrg parent = this.service.findById(parentId);
		if (parent == null) {
			throw new BaseException("上级组织不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long childComId = entity.getComId();
		if (childComId != null && parent.getComId() != null && !childComId.equals(parent.getComId())) {
			throw new BaseException("上级组织须与归属公司一致", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private void assertOrgTypeWritable(WoOrg incoming, WoOrg existing) {
		if (this.isAdmin(this.getUserId())) {
			return;
		}
		// 仅创建拦截 platform 类型；更新时表单回传原类型不视为越权，类型变更由下一段拦截
		if (existing == null && incoming != null && Constants.ENUM_TYPE_ORG_PLAT.equals(incoming.getOrgType())) {
			throw new BaseException("无权创建系统用户组", ResultCode.FORBIDDEN.getCode());
		}
		if (existing != null && incoming != null && incoming.getOrgType() != null
				&& !incoming.getOrgType().equals(existing.getOrgType())) {
			if (Constants.ENUM_TYPE_ORG_PLAT.equals(existing.getOrgType())
					|| Constants.ENUM_TYPE_ORG_PLAT.equals(incoming.getOrgType())) {
				throw new BaseException("无权修改组织类型", ResultCode.FORBIDDEN.getCode());
			}
			// 分群与普通组织互转会出现在另一套管理列表，仅允许新建时指定类型
			if (Constants.ENUM_TYPE_ORG_SEGMENT.equals(existing.getOrgType())
					|| Constants.ENUM_TYPE_ORG_SEGMENT.equals(incoming.getOrgType())) {
				throw new BaseException("分群类型不可与普通组织类型互转", ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	@ApiOperation(value = "组织角色权限", notes = "查询某个组织授予的角色（不继承）")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "domainId", value = "目标站点ID（多站租户组织授权时传入）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("role")
	public OrgRoleTree queryAuthRole(@RequestParam Map<String, String> authOrg) {
		long orgId = Long.parseLong(authOrg.get("orgId"));
		Long requestDomainId = parseOptionalLong(authOrg.get("domainId"));
		WoOrg org = this.service.findById(orgId);
		if (org != null) {
			this.assertEntityRowScope(org);
			this.grantVisibilityService.assertOrgAllowed(this.getUserId(), org, GrantAction.MEMBER_OPS);
		}
		return this.service.queryAllRoleTreeByOrgId(orgId, this.getUserId(), requestDomainId);
	}

	@ApiOperation(value = "组织授权", notes = "给组织关联系统角色")
	@PostMapping("auth")
	@OpLog(action = "组织角色授权", resourceType = "org", resourceId = "#roleOrg['orgId']")
	public Result authOrg(@ApiParam(value = "角色授权参数", required = true) @RequestBody Map<String, Object> roleOrg) {
		Long orgId = Long.parseLong(roleOrg.get("orgId").toString());
		Long comId = Long.parseLong(roleOrg.get("comId").toString());
		List<String> roleIds = (List<String>) roleOrg.get("roleIds");
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		WoOrg org = this.service.findById(orgId);
		if (org != null) {
			this.assertEntityRowScope(org);
			this.grantVisibilityService.assertOrgAllowed(curUserId, org, GrantAction.MEMBER_OPS);
		}
		// archId 以组织行为准，不再要求前端传体系
		Long archId = org != null && org.getArchId() != null ? org.getArchId()
				: Long.parseLong(roleOrg.get("archId").toString());
		this.assertRoleIdsAssignable(roleIds);
		this.service.authOrg(roleIds, orgId, archId, comId, curUserId, uip);

		return Result.ok("ok");
	}

	private void assertRoleIdsAssignable(List<String> roleIds) {
		if (this.isAdmin(this.getUserId()) || roleIds == null) {
			return;
		}
		Long callerComId = this.getTenantId();
		Long userId = this.getUserId();
		for (String raw : roleIds) {
			WoRole role = this.roleService.findById(Long.parseLong(raw));
			if (role != null) {
				DataScopeGuard.assertComId(callerComId, role.getComId());
				this.grantVisibilityService.assertRoleAllowed(userId, role, GrantAction.ASSIGN);
				this.adminScopeService.assertCanAssignRole(userId, role);
			}
		}
	}

	@ApiOperation(value = "组织添加成员", notes = "给组织添加成员")
	@PostMapping("user")
	@OpLog(action = "组织添加成员", resourceType = "org", resourceId = "#body.orgId")
	public Result userOrg(@ApiParam(value = "添加人员参数", required = true) @RequestBody OrgMemberAddBody body) {
		if (body == null || body.getOrgId() == null) {
			throw new BaseException("组织 id 不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long orgId = body.getOrgId();
		List<String> userIds = body.getUserIds();
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		WoOrg org = this.service.findById(orgId);
		Long bodyDomainId = body.getDomainId();
		Long bodyComId = body.getComId();
		org = this.resolveMemberOpsOrgForWrite(org, bodyDomainId, bodyComId);
		if (org != null && org.getId() != null) {
			orgId = org.getId();
		}
		if (org != null) {
			this.assertEntityRowScope(org);
			this.assertSegmentMemberOpsAllowed(org);
			this.grantVisibilityService.assertOrgAllowed(curUserId, org, GrantAction.MEMBER_OPS);
		}
		Long comId = this.resolveOrgMemberComId(org, body.getComId(), bodyDomainId);
		Long archId = org != null && org.getArchId() != null ? org.getArchId()
				: (body.getArchId() != null ? body.getArchId() : 0L);
		List<Long> ids = userIds == null ? java.util.Collections.emptyList()
				: userIds.stream().map(Long::parseLong).collect(Collectors.toList());
		this.grantVisibilityService.assertUsersInGrantorScope(curUserId, ids);
		for (Long targetUserId : ids) {
			this.adminScopeService.assertTargetIdentityLowerThanOperator(curUserId, targetUserId);
		}

		if (org != null && GrantVisibilityPolicy.isIdentityOrgCode(org.getOrgCode())) {
			String forwarded = this.forwardIdentityOrgMemberAdd(org.getOrgCode(), userIds, body, curUserId, uip);
			if (forwarded != null) {
				return Result.ok(forwarded);
			}
		}
		String message = this.service.userOrg(userIds, orgId, archId, comId, curUserId, uip);

		return Result.ok(message);
	}

	/**
	 * 平台身份组织加人：tadmin → 添加租户管理员（须受托租户 userComId）；
	 * dadmin → 绑定站点管理员（须目标站点 domainId）；admin/can_trust → 身份登记册（含组成员投影）。
	 */
	private String forwardIdentityOrgMemberAdd(String orgCode, List<String> userIds, OrgMemberAddBody body,
			Long curUserId, String uip) {
		if (Constants.TAdminOrgCode.equals(orgCode)) {
			Long userComId = body.getUserComId() != null ? body.getUserComId() : body.getComId();
			if (userComId == null || userComId <= 0) {
				throw new BaseException("添加租户管理员须指定受托租户（userComId）", ResultCode.VALIDATION_ERROR.getCode());
			}
			return this.companyService.addTenantAdmin(userIds, userComId, curUserId, uip);
		}
		if (Constants.DomainAdminOrgCode.equals(orgCode)) {
			Long domainId = body.getDomainId();
			if (domainId == null || domainId <= 0) {
				throw new BaseException("添加站点管理员须指定目标站点（domainId）", ResultCode.VALIDATION_ERROR.getCode());
			}
			Long comId = null;
			WoDomain domain = this.domainService.findById(domainId);
			if (domain != null) {
				comId = domain.getComId();
			}
			List<Long> ids = userIds.stream().map(Long::parseLong).collect(Collectors.toList());
			return this.domainService.bindDomainAdmins(domainId, ids, comId, curUserId, uip);
		}
		if (Constants.AdminOrgCode.equals(orgCode)) {
			return this.identityRegistryService.addPlatformAdmin(userIds, curUserId, uip);
		}
		if (Constants.CanTrustOrgCode.equals(orgCode)) {
			return this.identityRegistryService.addTrustMan(userIds, curUserId, uip);
		}
		return null;
	}

	@ApiOperation(value = "移除组织成员", notes = "移除组织成员")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "组织成员ID列表", dataTypeClass = List.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@DeleteMapping("staffDel")
	@OpLog(action = "组织移除成员", resourceType = "org", resourceId = "#params['orgId']")
	public Boolean removeOrgStaff(@RequestBody Map<String, Object> params) {
		Long orgId = Long.parseLong(ObjectUtils.string(params.get("orgId")));
		WoOrg org = this.service.findById(orgId);
		Long bodyDomainId = parseOptionalLong(ObjectUtils.string(params.get("domainId")));
		Long bodyComId = parseOptionalLong(ObjectUtils.string(params.get("comId")));
		org = this.resolveMemberOpsOrgForWrite(org, bodyDomainId, bodyComId);
		if (org != null && org.getId() != null) {
			orgId = org.getId();
		}
		if (org != null) {
			this.assertEntityRowScope(org);
			this.assertSegmentMemberOpsAllowed(org);
			this.grantVisibilityService.assertOrgAllowed(this.getUserId(), org, GrantAction.MEMBER_OPS);
		}
		List<Long> ids = ((List<String>) params.get("ids")).stream().map(Long::parseLong).collect(Collectors.toList());
		for (Long targetUserId : ids) {
			this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), targetUserId);
		}
		// 平台身份组织移除走登记册/绑定专用逻辑
		if (org != null && GrantVisibilityPolicy.isIdentityOrgCode(org.getOrgCode())) {
			if (this.forwardIdentityOrgMemberRemove(org.getOrgCode(), ids)) {
				return Boolean.TRUE;
			}
		}
		this.service.removeOrgStaff(ids, orgId);

		return Boolean.TRUE;
	}

	/** 平台身份组织移除：tadmin/dadmin/admin/can_trust 各走对应登记册或解绑逻辑。 */
	private boolean forwardIdentityOrgMemberRemove(String orgCode, List<Long> ids) {
		if (Constants.TAdminOrgCode.equals(orgCode)) {
			this.companyService.removeTenantAdmin(ids, null);
			return true;
		}
		if (Constants.DomainAdminOrgCode.equals(orgCode)) {
			ids.forEach(userId -> this.domainService.unbindDomainAdminByUser(userId, this.getUserId(), this.getUserIp()));
			return true;
		}
		if (Constants.AdminOrgCode.equals(orgCode)) {
			this.identityRegistryService.removePlatformAdmin(ids, this.getUserId(), this.getUserIp());
			return true;
		}
		if (Constants.CanTrustOrgCode.equals(orgCode)) {
			this.identityRegistryService.removeTrustMan(ids, this.getUserId(), this.getUserIp());
			return true;
		}
		return false;
	}

	@Override
	protected void postAdd(WoOrg org) {
		if (org != null) {
			this.orgClosureService.onOrgInserted(org.getId(), org.getParentId());
		}
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoOrg org) {
		if (org != null) {
			this.orgClosureService.onOrgUpdated(org.getId(), this.pendingOldParentId, org.getParentId());
			this.pendingOldParentId = null;
		}
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoOrg org) {
		if (org != null) {
			this.orgClosureService.onOrgDeleted(org.getId());
		}
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}

	@ApiOperation(value = "全部组织", notes = "下拉选用；超管传 comId 时剔除平台内置身份组织")
	@GetMapping("all")
	@Override
	public List<WoOrg> allEntities() {
		Long comId = this.grantVisibilityService.parseScopedTargetComIdFromRequest();
		List<WoOrg> list = super.allEntities();
		if (this.commonOperate.isAdmin(this.getUserId())) {
			if (this.shouldStripPlatformIdentityOrgsForScopedCom(comId)) {
				this.grantVisibilityService.stripPlatformIdentityWoOrgs(list, comId);
			}
		}
		return list;
	}

	@Override
	protected List<WoOrg> doFilter(List<WoOrg> res) {
		Long userId = this.getUserId();
		this.grantVisibilityService.filterWoOrgs(userId, res);

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoOrg plat = new WoOrg(Constants.TOP_ORG_ID, "平台");
			if (res.isEmpty()) {
				res.add(plat);
			}
			else {
				res.add(plat);
				res.sort(Comparator.comparing(WoOrg::getId));
			}
		}

		return res;
	}

	/** 无差异化业务逻辑的纯装饰性组织类型：不在任何 Controller/Service/GrantVisibilityPolicy 分支中被读取，
	 *  选中后与「组织机构」行为完全一致，仅在 org_type 落一个不同字符串——不作为可创建类型下发。 */
	private static final List<String> INERT_ORG_TYPES = Arrays.asList("role_org", "team", "group", "circle");

	@ApiOperation(value = "组织类型枚举", notes = "获取组织类型枚举列表")
	@GetMapping("type")
	public List<Map<String, Object>> fetchEnumAppType() {
		AdminScopeLevel level = this.adminScopeService.resolveLevel(this.getUserId());
		return Arrays.stream(OrgTypeEnum.values())
				.filter(v -> level == AdminScopeLevel.PLATFORM
						|| !v.getValue().equals(Constants.ENUM_TYPE_ORG_PLAT))
				.filter(v -> !INERT_ORG_TYPES.contains(v.getValue()))
				.map(item -> {
					Map<String, Object> em = new HashMap<>();
					em.put("label", item.getLabel());
					em.put("value", item.getValue());
					return em;
				}).collect(Collectors.toList());
	}

	private Long resolveOrgMemberComId(WoOrg org, Object rawComId, Long bodyDomainId) {
		Long requestComId = rawComId == null ? null
				: (rawComId instanceof Long ? (Long) rawComId : parseOptionalLong(rawComId.toString()));
		if (org == null) {
			return requestComId != null && requestComId > 0 && !Objects.equals(requestComId, Constants.TOP_COM_ID)
					? requestComId : Constants.TOP_COM_ID;
		}
		if (requestComId != null && requestComId > 0 && !Objects.equals(requestComId, Constants.TOP_COM_ID)) {
			return requestComId;
		}
		if (bodyDomainId != null && bodyDomainId > 0) {
			WoDomain bodyDomain = this.domainService.findById(bodyDomainId);
			if (bodyDomain != null && bodyDomain.getComId() != null && bodyDomain.getComId() > 0
					&& !Objects.equals(bodyDomain.getComId(), Constants.TOP_COM_ID)) {
				return bodyDomain.getComId();
			}
		}
		Long orgComId = org.getComId();
		if (orgComId != null && orgComId > 0 && !Objects.equals(orgComId, Constants.TOP_COM_ID)) {
			return orgComId;
		}
		Long domainId = org.getDomainId();
		if (domainId != null && domainId > 0) {
			WoDomain domain = this.domainService.findById(domainId);
			if (domain != null && domain.getComId() != null && domain.getComId() > 0
					&& !Objects.equals(domain.getComId(), Constants.TOP_COM_ID)) {
				return domain.getComId();
			}
		}
		Long tenantId = this.getTenantId();
		if (tenantId != null && tenantId > 0 && !Objects.equals(tenantId, Constants.TOP_COM_ID)) {
			return tenantId;
		}
		Long operatorComId = this.commonOperate.getOperatorComId(this.getUserId());
		if (operatorComId != null && operatorComId > 0 && !Objects.equals(operatorComId, Constants.TOP_COM_ID)) {
			return operatorComId;
		}
		throw new BaseException("请选择目标租户后再添加成员", ResultCode.VALIDATION_ERROR.getCode());
	}

	/**
	 * 会员分群成员写入须落到站点实例组织，禁止写平台模板组织。
	 */
	private WoOrg resolveMemberOpsOrgForWrite(WoOrg org, Long bodyDomainId, Long bodyComId) {
		if (org == null || !this.orgOperationalKindBridge.isOperationalKind(org)) {
			return org;
		}
		if (org.getDomainId() != null && org.getDomainId() > 0) {
			return org;
		}
		if (bodyDomainId == null || bodyDomainId <= 0) {
			throw new BaseException("分群成员操作须指定目标站点（domainId）", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long scopeComId = bodyComId;
		if (scopeComId == null || scopeComId <= 0 || Objects.equals(scopeComId, Constants.TOP_COM_ID)) {
			scopeComId = this.orgOperationalKindBridge.resolveComIdForDomain(bodyDomainId, this.getTenantId());
		}
		return this.orgOperationalKindBridge.resolveOperationalInstance(org, bodyDomainId, scopeComId);
	}

	/** 门户站禁止改会员分群成员；分群租户须与站点租户一致。 */
	private void assertSegmentMemberOpsAllowed(WoOrg org) {
		if (org == null) {
			return;
		}
		boolean memberKind = Constants.ENUM_TYPE_ORG_SEGMENT.equals(org.getOrgType())
				|| isMemberKindOrgCode(org.getOrgCode());
		if (!memberKind) {
			return;
		}
		Long domainId = org.getDomainId();
		if (domainId == null || domainId <= 0) {
			if (org.getComId() != null && org.getComId() == 0) {
				throw new BaseException("平台门户站不允许维护会员分群成员", ResultCode.VALIDATION_ERROR.getCode());
			}
			return;
		}
		WoDomain domain = this.domainService.findById(domainId);
		if (domain == null) {
			return;
		}
		Long domainComId = domain.getComId();
		if (domainComId == null || domainComId == 0) {
			throw new BaseException("门户站不允许维护会员分群成员", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long orgComId = org.getComId();
		if (orgComId != null && orgComId == 0) {
			throw new BaseException("分群所属租户与站点不一致，请联系管理员核对数据", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private static boolean isMemberKindOrgCode(String orgCode) {
		return "user".equals(orgCode) || "freeuser".equals(orgCode) || "vip".equals(orgCode)
				|| "un_active".equals(orgCode);
	}

	private static Long parseOptionalLong(String raw) {
		if (raw == null || raw.trim().isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(raw.trim());
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}
}
