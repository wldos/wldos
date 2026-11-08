/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.rbac.DataScope;
import io.github.wldos.platform.support.rbac.DataScopeGuard;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.platform.support.rbac.EffectiveScopeCache;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import io.github.wldos.platform.support.rbac.AdminWriteScopeStamper;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoRoleScope;
import com.wldos.platform.core.enums.RoleTypeEnum;
import com.wldos.platform.core.service.AdminScopeService;
import com.wldos.platform.core.service.GrantVisibilityService;
import com.wldos.platform.core.service.RoleReferenceGuard;
import com.wldos.platform.core.service.RoleAuthBatchService;
import com.wldos.platform.core.service.OperationalTreeDeduper;
import com.wldos.platform.core.service.RoleOperationalKindBridge;
import com.wldos.platform.core.service.PlatformOperatorSettingsService;
import com.wldos.platform.core.service.RoleScopeService;
import com.wldos.platform.core.service.RoleService;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;
import io.github.wldos.platform.support.rbac.JurisdictionOverlayAxis;
import io.github.wldos.platform.support.rbac.RoleShareScope;
import io.github.wldos.platform.support.rbac.kind.SystemRoleKind;
import com.wldos.platform.core.vo.Role;
import com.wldos.platform.core.vo.RoleAuthBatchApplyResultVO;
import com.wldos.platform.core.vo.RoleAuthBatchBody;
import com.wldos.platform.core.vo.RoleAuthBatchPreviewVO;
import com.wldos.platform.core.vo.RoleAuthBatchRollbackBody;
import com.wldos.platform.core.vo.RoleAuthDriftItem;
import com.wldos.platform.core.vo.RoleScopeBatchBody;
import com.wldos.platform.core.vo.RoleScopeCopyBody;
import com.wldos.platform.core.vo.RoleScopeSaveBody;

import com.wldos.platform.core.vo.RoleResTree;
import org.springframework.context.annotation.Lazy;
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
 * 角色相关controller。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "角色管理")
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/role")
public class RoleController extends EntityController<RoleService, WoRole> {

	private final AdminScopeService adminScopeService;

	private final RoleScopeService roleScopeService;

	private final RoleReferenceGuard roleReferenceGuard;

	private final GrantVisibilityService grantVisibilityService;

	private final RoleOperationalKindBridge roleOperationalKindBridge;

	private final RoleAuthBatchService roleAuthBatchService;

	private final PlatformOperatorSettingsService platformOperatorSettingsService;

	public RoleController(
			AdminScopeService adminScopeService,
			RoleScopeService roleScopeService,
			RoleReferenceGuard roleReferenceGuard,
			GrantVisibilityService grantVisibilityService,
			RoleOperationalKindBridge roleOperationalKindBridge,
			RoleAuthBatchService roleAuthBatchService,
			@Lazy PlatformOperatorSettingsService platformOperatorSettingsService) {
		this.adminScopeService = adminScopeService;
		this.roleScopeService = roleScopeService;
		this.roleReferenceGuard = roleReferenceGuard;
		this.grantVisibilityService = grantVisibilityService;
		this.roleOperationalKindBridge = roleOperationalKindBridge;
		this.roleAuthBatchService = roleAuthBatchService;
		this.platformOperatorSettingsService = platformOperatorSettingsService;
	}
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询参数
	 * @return 分页列表页
	 */
	@ApiOperation(value = "角色列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "roleName", value = "角色名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（按站点筛选会员角色实例）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管跨宇宙视图）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<Role> listQuery(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		Long scopedTargetComId = this.grantVisibilityService.parseScopedTargetComId(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, WoRole.class);
		Long targetDomainId = this.parseRoleTargetDomainId(params);
		if (targetDomainId != null && targetDomainId > 0) {
			pageQuery.getCondition().remove("domainId");
			pageQuery.removeParam(Constants.COMMON_KEY_DOMAIN_COLUMN);
			Long comId = this.roleOperationalKindBridge.resolveComIdForDomain(targetDomainId, this.getTenantId());
			DataScopeQueryHelper.applySharedDomainAdminRead(
					pageQuery,
					comId,
					java.util.Collections.singletonList(targetDomainId));
		}
		PageData<Role> page = this.service.execQueryForTree(new Role(), new WoRole(), pageQuery, Constants.TOP_ROLE_ID);
		if (targetDomainId != null && targetDomainId > 0) {
			this.reconcileOperationalInstances(page.getRows(), targetDomainId);
			OperationalTreeDeduper.dedupeRoleTree(page.getRows(), targetDomainId);
		}
		this.grantVisibilityService.filterRoleTree(this.getUserId(), page.getRows());
		if (this.commonOperate.isAdmin(this.getUserId())) {
			if (this.shouldStripPlatformIdentityRolesForScopedCom(scopedTargetComId)) {
				this.grantVisibilityService.stripPlatformIdentityRoleTree(page.getRows(), scopedTargetComId);
			}
			if (scopedTargetComId != null) {
				this.grantVisibilityService.filterRoleTreeByScopedComId(page.getRows(), scopedTargetComId);
			}
		}
		this.decorateManaged(page.getRows());
		return page;
	}

	private void reconcileOperationalInstances(List<Role> roles, Long targetDomainId) {
		if (roles == null || targetDomainId == null) {
			return;
		}
		Long comId = this.roleOperationalKindBridge.resolveComIdForDomain(targetDomainId, this.getTenantId());
		Iterator<Role> iterator = roles.iterator();
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role == null) {
				iterator.remove();
				continue;
			}
			if (role.getChildren() != null && !role.getChildren().isEmpty()) {
				this.reconcileOperationalInstances(role.getChildren(), targetDomainId);
			}
			if (role.getRoleCode() == null || !GrantVisibilityPolicy.isOperationalRoleCode(role.getRoleCode())) {
				continue;
			}
			if (role.getDomainId() != null && role.getDomainId() > 0) {
				continue;
			}
			if (!RoleOperationalKindBridge.isSiteBootstrapRoleCode(role.getRoleCode())) {
				iterator.remove();
				continue;
			}
			WoRole wo = new WoRole();
			wo.setId(role.getId());
			wo.setRoleCode(role.getRoleCode());
			wo.setRoleType(role.getRoleType());
			wo.setComId(role.getComId());
			wo.setDomainId(role.getDomainId());
			WoRole instance = this.roleOperationalKindBridge.tryResolveOperationalInstance(wo, targetDomainId, comId);
			if (instance == null) {
				iterator.remove();
				continue;
			}
			role.setId(instance.getId());
			role.setDomainId(instance.getDomainId());
			role.setComId(instance.getComId());
		}
		roles.removeIf(r -> r != null && GrantVisibilityPolicy.isOperationalRoleCode(r.getRoleCode())
				&& r.getDomainId() != null && r.getDomainId() > 0 && !targetDomainId.equals(r.getDomainId()));
	}

	private void decorateManaged(List<Role> roles) {
		if (roles == null) {
			return;
		}
		Long userId = this.getUserId();
		for (Role role : roles) {
			WoRole wo = new WoRole();
			wo.setId(role.getId());
			wo.setRoleCode(role.getRoleCode());
			wo.setRoleType(role.getRoleType());
			wo.setComId(role.getComId());
			wo.setDomainId(role.getDomainId());
			wo.setDataPolicy(role.getDataPolicy());
			role.setManaged(this.adminScopeService.canManageRole(userId, wo));
			role.setAssignable(this.adminScopeService.canAssignRole(userId, wo));
			role.setSystemBuiltIn(SystemRoleKind.ofRoleCode(role.getRoleCode()) != null
					&& role.getDomainId() != null && role.getDomainId() > 0);
			role.setKindTemplate(SystemRoleKind.ofRoleCode(role.getRoleCode()) != null
					&& role.getComId() != null && role.getComId() == 0L
					&& (role.getDomainId() == null || role.getDomainId() <= 0));
			role.setDeletable(!GrantVisibilityPolicy.isUndeletableSystemGrantRole(
					role.getRoleCode(), role.getRoleType(), role.getComId(), role.getDomainId(), false));
			if (role.getChildren() != null) {
				this.decorateManaged(role.getChildren());
			}
		}
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoRole role = this.service.findById(id);
		if (role != null) {
			Long userId = this.getUserId();
			if (!this.adminScopeService.canManageRole(userId, role)
					&& !this.adminScopeService.canAssignRole(userId, role)) {
				throw new BaseException("无权访问该角色", ResultCode.FORBIDDEN.getCode());
			}
			this.assertRoleDataScope(role);
		}
		return role;
	}

	/**
	 * 查询角色授权资源树。
	 *
	 * @param authRole 授权角色参数
	 * @return 授权资源树
	 */
	@ApiOperation(value = "角色授权资源树", notes = "查询角色授权资源树")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleId", value = "角色ID", dataTypeClass = Long.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "targetDomainId", value = "目标站点 ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（同 targetDomainId）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("res")
	public RoleResTree queryAuthRes(@RequestParam Map<String, String> authRole) {
		Long roleId = Long.parseLong(authRole.get("roleId"));
		WoRole role = this.service.findById(roleId);
		Long curUserId = this.getUserId();
		Long targetDomainId = this.parseRoleTargetDomainId(authRole);
		if (role != null) {
			Long comId = this.roleOperationalKindBridge.resolveComIdForDomain(targetDomainId, this.getTenantId());
			role = this.roleOperationalKindBridge.resolveOperationalInstance(role, targetDomainId, comId);
			this.adminScopeService.assertCanAssignRole(curUserId, role);
			roleId = role.getId();
		}
		Long grantDomainId = this.resolveGrantResourceDomainId(targetDomainId);
		Long grantComId = this.roleOperationalKindBridge.resolveComIdForDomain(grantDomainId, this.getTenantId());
		Set<Long> allowed = this.isAdmin(curUserId) ? null
				: this.adminScopeService.resolveGrantorEffectiveResourceIds(grantDomainId, grantComId, curUserId);
		Long overlayDomainId = this.resolveOverlayDomainIdForResTree(curUserId, targetDomainId);
		return this.service.queryAllResTreeByRoleId(roleId, allowed, overlayDomainId);
	}

	@ApiOperation(value = "角色授权", notes = "给角色授权资源")
	@PostMapping("auth")
	@OpLog(action = "角色资源授权", resourceType = "role", resourceId = "#resRole['roleId']")
	public Result authRole(@ApiParam(value = "角色资源授权参数", required = true) @RequestBody Map<String, Object> resRole) {
		Long roleId = Long.parseLong(resRole.get("roleId").toString());
		List<String> resIds = (List<String>) resRole.get("resIds");
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		WoRole role = this.service.findById(roleId);
		Long targetDomainId = this.parseRoleTargetDomainId(resRole);
		if (role != null) {
			Long comId = this.roleOperationalKindBridge.resolveComIdForDomain(targetDomainId, this.getTenantId());
			role = this.roleOperationalKindBridge.resolveOperationalInstance(role, targetDomainId, comId);
			roleId = role.getId();
			this.adminScopeService.assertCanAssignRole(curUserId, role);
		}
		Long grantDomainId = this.resolveGrantResourceDomainId(targetDomainId);
		Long grantComId = this.roleOperationalKindBridge.resolveComIdForDomain(grantDomainId, this.getTenantId());
		this.adminScopeService.assertCanAuthResources(grantDomainId, grantComId, curUserId, roleId, resIds);
		JurisdictionOverlayAxis axis;
		if (role != null && this.roleOperationalKindBridge.isOperationalKind(role)) {
			axis = JurisdictionOverlayAxis.baseline();
		}
		else if (role != null && GrantVisibilityPolicy.isIdentityRoleCode(role.getRoleCode())) {
			axis = this.resolveIdentityAuthOverlayAxis(role.getRoleCode(), curUserId);
		}
		else {
			axis = this.resolveAuthOverlayAxis(curUserId);
		}
		this.service.authRole(resIds, roleId, axis, curUserId, uip);
		this.refreshAuth();

		return Result.ok("ok");
	}

	@ApiOperation("读取角色管理范围")
	@GetMapping("scopes")
	public Result<List<WoRoleScope>> listRoleScopes(@RequestParam Long roleId) {
		WoRole role = this.service.findById(roleId);
		if (role != null) {
			this.adminScopeService.assertCanAssignRole(this.getUserId(), role);
		}
		JurisdictionOverlayAxis axis = this.resolveScopeOverlayAxis(this.getUserId(), role);
		return Result.ok(this.roleScopeService.listByRoleId(roleId, axis));
	}

	@ApiOperation("读取角色直接选用的定制字段")
	@GetMapping("scopes/adopted")
	public Result<List<String>> listDirectAdoptedDimensions(@RequestParam Long roleId) {
		WoRole role = this.service.findById(roleId);
		if (role != null) {
			this.adminScopeService.assertCanAssignRole(this.getUserId(), role);
		}
		return Result.ok(this.roleScopeService.listDirectAdoptedDictCodes(roleId));
	}

	@ApiOperation("保存角色管理范围")
	@PostMapping("scopes")
	public Result<Void> saveRoleScopes(@RequestBody RoleScopeSaveBody body, @RequestParam Long roleId) {
		WoRole role = this.service.findById(roleId);
		if (role != null) {
			this.adminScopeService.assertCanAssignRole(this.getUserId(), role);
		}
		JurisdictionOverlayAxis axis = this.resolveScopeOverlayAxis(this.getUserId(), role);
		this.roleScopeService.saveRoleScopeBundle(this.getUserId(), roleId, body, axis);
		return Result.ok(null);
	}

	@ApiOperation("从源角色复制管理范围")
	@PostMapping("scopes/copy")
	public Result<Void> copyRoleScopes(@RequestBody RoleScopeCopyBody body) {
		if (body == null || body.getSourceRoleId() == null || body.getTargetRoleId() == null) {
			throw new BaseException("源角色与目标角色不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoRole source = this.service.findById(body.getSourceRoleId());
		WoRole target = this.service.findById(body.getTargetRoleId());
		if (source != null) {
			this.adminScopeService.assertCanAssignRole(this.getUserId(), source);
		}
		if (target != null) {
			this.adminScopeService.assertCanAssignRole(this.getUserId(), target);
		}
		JurisdictionOverlayAxis axis = this.resolveScopeOverlayAxis(this.getUserId(), target);
		this.roleScopeService.copyScopesFromRole(this.getUserId(), body, axis);
		return Result.ok(null);
	}

	@ApiOperation("批量将源角色管理范围应用到多个目标角色")
	@PostMapping("scopes/batch")
	public Result<Void> batchApplyRoleScopes(@RequestBody RoleScopeBatchBody body) {
		if (body == null || body.getSourceRoleId() == null) {
			throw new BaseException("源角色不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoRole source = this.service.findById(body.getSourceRoleId());
		if (source != null) {
			this.adminScopeService.assertCanAssignRole(this.getUserId(), source);
		}
		if (body.getTargetRoleIds() != null) {
			for (Long targetRoleId : body.getTargetRoleIds()) {
				if (targetRoleId == null) {
					continue;
				}
				WoRole target = this.service.findById(targetRoleId);
				if (target != null) {
					this.adminScopeService.assertCanAssignRole(this.getUserId(), target);
				}
				RoleScopeCopyBody copyBody = new RoleScopeCopyBody();
				copyBody.setSourceRoleId(body.getSourceRoleId());
				copyBody.setTargetRoleId(targetRoleId);
				JurisdictionOverlayAxis targetAxis = this.resolveScopeOverlayAxis(this.getUserId(), target);
				this.roleScopeService.copyScopesFromRole(this.getUserId(), copyBody, targetAxis);
			}
		}
		return Result.ok(null);
	}

	@ApiOperation("会员角色授权批量推送预览")
	@PostMapping("auth/batch/preview")
	public Result<RoleAuthBatchPreviewVO> previewAuthBatch(@RequestBody RoleAuthBatchBody body) {
		Long userId = this.getUserId();
		RoleAuthBatchPreviewVO preview = this.roleAuthBatchService.preview(
				body,
				userId,
				this.getTenantId(),
				this.isAdmin(userId),
				this.commonOperate.isTenantAdmin(userId),
				this.commonOperate.listBoundDomainIds(userId));
		return Result.ok(preview);
	}

	@ApiOperation("会员角色授权批量推送")
	@PostMapping("auth/batch")
	@OpLog(action = "角色授权批量推送", resourceType = "role")
	public Result<RoleAuthBatchApplyResultVO> applyAuthBatch(@RequestBody RoleAuthBatchBody body) {
		Long userId = this.getUserId();
		RoleAuthBatchApplyResultVO result = this.roleAuthBatchService.apply(
				body,
				userId,
				this.getUserIp(),
				this.getTenantId(),
				this.isAdmin(userId),
				this.commonOperate.isTenantAdmin(userId),
				this.commonOperate.listBoundDomainIds(userId));
		this.refreshAuth();
		return Result.ok(result);
	}

	@ApiOperation("回滚最近一次批量推送（按推送前快照恢复各站默认授权，单次有效，7 天内）")
	@PostMapping("auth/batch/rollback")
	@OpLog(action = "角色授权批量推送回滚", resourceType = "role")
	public Result<Void> rollbackAuthBatch(@RequestBody RoleAuthBatchRollbackBody body) {
		Long userId = this.getUserId();
		this.roleAuthBatchService.rollback(
				body != null ? body.getSnapshotId() : null,
				userId,
				this.getUserIp(),
				this.getTenantId(),
				this.isAdmin(userId),
				this.commonOperate.isTenantAdmin(userId),
				this.commonOperate.listBoundDomainIds(userId));
		this.refreshAuth();
		return Result.ok(null);
	}

	@ApiOperation("会员角色授权差异对比（只读）")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleCode", value = "角色编码", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "baselineDomainId", value = "基准站点 id，空则对比平台模板", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("auth/drift")
	public Result<List<RoleAuthDriftItem>> authDrift(
			@RequestParam String roleCode,
			@RequestParam(required = false) Long baselineDomainId) {
		Long userId = this.getUserId();
		List<RoleAuthDriftItem> items = this.roleAuthBatchService.driftCompare(
				roleCode,
				baselineDomainId,
				this.getTenantId(),
				this.isAdmin(userId),
				this.commonOperate.isTenantAdmin(userId),
				this.commonOperate.listBoundDomainIds(userId));
		return Result.ok(items);
	}

	@Override
	protected void postAdd(WoRole role) {
		EffectiveScopeCache.invalidateAll(this.cache);
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoRole role) {
		EffectiveScopeCache.invalidateAll(this.cache);
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoRole role) {
		EffectiveScopeCache.invalidateAll(this.cache);
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		EffectiveScopeCache.invalidateAll(this.cache);
		this.refreshAuth();
	}

	@Override
	protected void preUpdate(WoRole role) {
		WoRole existing = role.getId() != null ? this.service.findById(role.getId()) : null;
		if (existing != null) {
			this.assertRoleTypeWritable(role, existing);
			this.assertRoleDataScope(existing);
			if (this.adminScopeService.canManageRole(this.getUserId(), existing)) {
				this.assertSystemRoleMutable(existing, role);
				if (!Objects.equals(existing.getParentId(), role.getParentId())) {
					this.assertParentRoleGrantable(role);
				}
				if (!this.isAdmin(this.getUserId())) {
					if (this.isMultiTenancy()) {
						DataScopeGuard.assertComIdImmutable(existing.getComId(), role.getComId());
					}
					DataScopeGuard.assertDomainIdImmutable(existing.getDomainId(), role.getDomainId());
				}
			}
			else {
				this.adminScopeService.assertCanAssignRole(this.getUserId(), existing);
				this.restrictAssignOnlyUpdate(existing, role);
			}
		}
		this.assertDataPolicyGrant(role.getDataPolicy());
	}

	/** 仅 ASSIGN 无 MANAGE 时：编码/类型/归属/结构不可改，名称与授权等可改。 */
	private void restrictAssignOnlyUpdate(WoRole existing, WoRole incoming) {
		incoming.setRoleCode(existing.getRoleCode());
		incoming.setRoleType(existing.getRoleType());
		incoming.setParentId(existing.getParentId());
		incoming.setComId(existing.getComId());
		incoming.setDomainId(existing.getDomainId());
		incoming.setIsSystem(existing.getIsSystem());
	}

	private Long parseRoleTargetDomainId(Map<String, ?> params) {
		Long userId = this.getUserId();
		boolean sessionSiteOnly = userId != null
				&& !this.isAdmin(userId)
				&& this.commonOperate.isDomainAdmin(userId)
				&& !this.commonOperate.isTenantAdmin(userId);
		return this.roleOperationalKindBridge.parseTargetDomainId(
				params,
				this.getDomainId(),
				this.commonOperate.listBoundDomainIds(userId),
				sessionSiteOnly);
	}

	/** 授权资源树使用的站点：列表所选站点优先，否则当前访问站点。 */
	private Long resolveGrantResourceDomainId(Long targetDomainId) {
		if (targetDomainId != null && targetDomainId > 0L) {
			return targetDomainId;
		}
		return this.getDomainId();
	}

	private Long resolveOverlayDomainIdForResTree(Long curUserId, Long targetDomainId) {
		if (targetDomainId != null && targetDomainId > 0L) {
			return targetDomainId;
		}
		return this.isAdmin(curUserId) ? null : targetDomainId;
	}

	private JurisdictionOverlayAxis resolveAuthOverlayAxis(Long userId) {
		if (userId != null && this.isAdmin(userId)) {
			return JurisdictionOverlayAxis.baseline();
		}
		if (userId != null && this.commonOperate.isTenantAdmin(userId)) {
			return JurisdictionOverlayAxis.tenant(this.getTenantId());
		}
		if (userId != null && this.commonOperate.isDomainAdmin(userId)) {
			List<Long> bound = this.commonOperate.listBoundDomainIds(userId);
			Long domainId = bound != null && !bound.isEmpty() ? bound.get(0) : this.getDomainId();
			return JurisdictionOverlayAxis.domain(domainId);
		}
		return JurisdictionOverlayAxis.baseline();
	}

	private JurisdictionOverlayAxis resolveIdentityAuthOverlayAxis(String roleCode, Long userId) {
		if (Constants.TenantAdminRoleCode.equals(roleCode) && userId != null
				&& this.commonOperate.isTenantAdmin(userId) && !this.isAdmin(userId)) {
			return JurisdictionOverlayAxis.tenant(this.getTenantId());
		}
		return JurisdictionOverlayAxis.baseline();
	}

	private JurisdictionOverlayAxis resolveScopeOverlayAxis(Long userId, WoRole role) {
		if (userId != null && this.isAdmin(userId)) {
			return JurisdictionOverlayAxis.baseline();
		}
		if (role != null && RoleShareScope.isShared(role.getShareScope())) {
			return resolveAuthOverlayAxis(userId);
		}
		return JurisdictionOverlayAxis.baseline();
	}

	@Override
	protected void preAdd(WoRole role) {
		this.assertRoleTypeWritable(role, null);
		this.applyWriteScope(role, WriteScopeTarget.TENANT_AXIS);
		if (this.isAdmin(this.getUserId())) {
			Long resolvedComId = this.roleOperationalKindBridge.resolveComIdForDomain(
					role.getDomainId(), this.getTenantId());
			AdminWriteScopeStamper.ensureComIdFromDomain(role, role.getDomainId(), resolvedComId);
		}
		this.assertDataPolicyGrant(role.getDataPolicy());
		this.assertParentRoleGrantable(role);
		if (role.getShareScope() == null || role.getShareScope().isEmpty()) {
			role.setShareScope(RoleShareScope.SHARED.getValue());
		}
	}

	@Override
	protected void preDelete(WoRole entity) {
		if (entity != null && entity.getId() != null) {
			WoRole existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertRoleDataScope(existing);
				this.adminScopeService.assertCanManageRole(this.getUserId(), existing);
				this.assertSystemRoleDeletable(existing);
				this.roleReferenceGuard.assertDeletable(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			WoRole existing = this.service.findById(Long.parseLong(raw.toString()));
			if (existing != null) {
				this.assertRoleDataScope(existing);
				this.adminScopeService.assertCanManageRole(this.getUserId(), existing);
				this.assertSystemRoleDeletable(existing);
				this.roleReferenceGuard.assertDeletable(existing);
			}
		}
	}

	/**
	 * 系统保留角色写守卫：{@code isAdmin()} 只按 {@code wo_org.org_code=admin} 组织成员判定（见
	 * {@code CommonOperationImpl#listSuperAdmin}），不依赖任何 role_code——超管本人改动 admin/admins 等系统角色
	 * 不会波及自身身份判定与鉴权短路（{@code termOpener.isAdmin} 整段放行与角色无关），故仅拦截非超管操作者；
	 * 超管本人不设限制，避免限制其对平台角色体系的正常运维权力。
	 */
	private void assertSystemRoleMutable(WoRole existing, WoRole incoming) {
		if (!existing.isSystemReserved()) {
			return;
		}
		if (this.isAdmin(this.getUserId())) {
			incoming.setIsSystem(existing.getIsSystem());
			return;
		}
		if (incoming.getRoleCode() != null && !incoming.getRoleCode().equals(existing.getRoleCode())) {
			throw new BaseException("系统保留角色禁止修改标识（role_code）");
		}
		incoming.setIsSystem(existing.getIsSystem());
	}

	private void assertSystemRoleDeletable(WoRole existing) {
		if (existing == null) {
			return;
		}
		if (GrantVisibilityPolicy.isUndeletableSystemGrantRole(
				existing.getRoleCode(), existing.getRoleType(),
				existing.getComId(), existing.getDomainId(), existing.isSystemReserved())) {
			throw new BaseException("系统内置角色禁止删除");
		}
		if (existing.isSystemReserved()) {
			throw new BaseException("系统保留角色禁止删除");
		}
	}

	private void assertRoleTypeWritable(WoRole incoming, WoRole existing) {
		if (this.isAdmin(this.getUserId())) {
			return;
		}
		// 仅创建拦截 sys 类型；更新时表单回传原类型不视为越权，类型变更由下一段拦截
		if (existing == null && incoming != null && RoleTypeEnum.SYS_ROLE.toString().equals(incoming.getRoleType())) {
			throw new BaseException("无权创建系统角色", ResultCode.FORBIDDEN.getCode());
		}
		if (existing != null && incoming != null && incoming.getRoleType() != null
				&& !incoming.getRoleType().equals(existing.getRoleType())) {
			if (RoleTypeEnum.SYS_ROLE.toString().equals(existing.getRoleType())
					|| RoleTypeEnum.SYS_ROLE.toString().equals(incoming.getRoleType())) {
				throw new BaseException("无权修改角色类型", ResultCode.FORBIDDEN.getCode());
			}
		}
	}

	private void assertRoleDataScope(WoRole role) {
		if (role == null || this.isAdmin(this.getUserId())) {
			return;
		}
		Long userId = this.getUserId();
		DataScopeGuard.assertComId(this.getTenantId(), role.getComId());
		if (this.commonOperate.isTenantAdmin(userId)) {
			return;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			Long rowDomainId = role.getDomainId();
			if (rowDomainId != null && rowDomainId > 0) {
				java.util.List<Long> bound = this.commonOperate.listBoundDomainIds(userId);
				if (bound != null && bound.contains(rowDomainId)) {
					return;
				}
			}
		}
		DataScopeGuard.assertDomainId(this.getDomainId(), role.getDomainId());
	}

	/**
	 * 父角色挂接：子角色会继承父级授权，禁止挂到平台身份角色下。
	 * 非超管另须对父角色可见且可 ASSIGN、行归属在自身 scope 内。
	 */
	private void assertParentRoleGrantable(WoRole role) {
		Long parentId = role.getParentId();
		if (parentId == null || parentId <= 0) {
			return;
		}
		WoRole parent = this.service.findById(parentId);
		if (parent == null) {
			throw new BaseException("父角色不存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (GrantVisibilityPolicy.isIdentityRoleCode(parent.getRoleCode())) {
			throw new BaseException("不允许挂载到系统内置身份角色下", ResultCode.FORBIDDEN.getCode());
		}
		if (!this.isAdmin(this.getUserId())) {
			this.assertRoleDataScope(parent);
			this.adminScopeService.assertCanAssignRole(this.getUserId(), parent);
		}
	}

	private void assertDataPolicyGrant(String dataPolicy) {
		if (ObjectUtils.isBlank(dataPolicy)) {
			return;
		}
		DataScope scope = DataScope.ofCode(dataPolicy);
		if (scope == null) {
			throw new BaseException("无效的数据策略: " + dataPolicy);
		}
		if (!this.adminScopeService.canGrantDataPolicy(this.getUserId(), scope)) {
			throw new BaseException("无权配置数据策略: " + dataPolicy, ResultCode.FORBIDDEN.getCode());
		}
	}

	@ApiOperation(value = "全部角色", notes = "下拉选用；超管传 comId 时剔除平台内置身份角色")
	@GetMapping("all")
	@Override
	public List<WoRole> allEntities() {
		Long comId = this.grantVisibilityService.parseScopedTargetComIdFromRequest();
		List<WoRole> list = super.allEntities();
		if (this.commonOperate.isAdmin(this.getUserId())) {
			if (this.shouldStripPlatformIdentityRolesForScopedCom(comId)) {
				this.grantVisibilityService.stripPlatformIdentityWoRoles(list, comId);
			}
		}
		return list;
	}

	/**
	 * 超管选「平台」轴（comId=0）时保留 I 类身份角色；选任意具体租户（含平台运营主体标准租户）时隐藏 I 类，只维护业务角色树。
	 */
	private boolean shouldStripPlatformIdentityRolesForScopedCom(Long scopedComId) {
		if (scopedComId == null || scopedComId <= 0) {
			return false;
		}
		return this.commonOperate.isAdmin(this.getUserId());
	}

	/**
	 * 查询角色列表时追加平台根角色
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoRole> doFilter(List<WoRole> res) {
		Long userId = this.getUserId();
		this.grantVisibilityService.filterWoRoles(userId, res);

		if (this.isPlatformAdmin(this.getTenantId())) {
			WoRole plat = new WoRole(Constants.TOP_ROLE_ID, "根角色");
			if (res.isEmpty()) {
				res.add(plat);
			}
			else{
				res.add(plat);
				res.sort(Comparator.comparing(WoRole::getId));
			}
		}

		return res;
	}
}