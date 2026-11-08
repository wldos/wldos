/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.platform.auth.service.LoginAuthService;
import com.wldos.platform.auth.vo.AdminPasswdModifyParams;
import com.wldos.platform.auth.vo.Login;
import com.wldos.platform.auth.vo.PasswdModifyParams;
import com.wldos.platform.auth.vo.Register;
import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;
import io.github.wldos.platform.support.rbac.AdminWriteScopeStamper;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import io.github.wldos.platform.support.resource.vo.Menu;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoUser;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import com.wldos.platform.core.enums.UserStatusEnum;
import com.wldos.platform.core.service.AdminScopeService;
import com.wldos.platform.core.service.OptionsService;
import com.wldos.platform.core.service.OrgService;
import com.wldos.platform.core.service.OrgOperationalKindBridge;
import com.wldos.platform.core.service.RoleBindingService;
import com.wldos.platform.core.service.UserService;
import com.wldos.platform.core.vo.AdminHeaderHelpConfig;
import com.wldos.platform.core.vo.AdminHomeConfigVO;
import com.wldos.platform.core.vo.AdminWorkOrgVO;
import com.wldos.platform.core.vo.UserAuth;
import com.wldos.platform.core.vo.UserAuthContext;
import com.wldos.platform.core.vo.UserOrgRoleVO;
import com.wldos.platform.core.vo.UserOrgVO;
import com.wldos.platform.core.vo.UserRoleBindBody;
import com.wldos.platform.core.vo.UserRoleSourceVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 用户管理相关controller。
 * 租户管理员不具备删除用户权限，这是因为注销属于用户权力，租户不能删除平台用户。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "用户管理（后台）")
@RestController
@RequestMapping("admin/sys/user")
public class UserAdminController extends EntityController<UserService, WoUser> {

	private final LoginAuthService loginAuthService;

	private final ThreadLocal<String> previousUserStatusHolder = new ThreadLocal<>();

	@Autowired
	private OptionsService optionsService;

	@Autowired
	private DomainDao domainDao;

	@Autowired
	private AdminScopeService adminScopeService;

	@Autowired
	private RoleBindingService roleBindingService;

	@Autowired
	@Lazy
	private OrgService orgService;

	@Autowired
	@Lazy
	private OrgOperationalKindBridge orgOperationalKindBridge;

	public UserAdminController(LoginAuthService loginAuthService) {
		this.loginAuthService = loginAuthService;
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoUser user = this.service.findById(id);
		if (user != null) {
			this.assertEntityRowScope(user);
		}
		return user;
	}

	@Override
	protected void preUpdate(WoUser entity) {
		if (entity != null && entity.getId() != null) {
			WoUser existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), existing.getId());
				this.previousUserStatusHolder.set(existing.getStatus());
			}
		}
	}

	@Override
	protected void postUpdate(WoUser entity) {
		try {
			if (entity != null && entity.getId() != null
					&& UserStatusEnum.normal.getValue().equals(entity.getStatus())) {
				String previousStatus = this.previousUserStatusHolder.get();
				if (UserStatusEnum.notActive.getValue().equals(previousStatus)
						|| UserStatusEnum.locked.getValue().equals(previousStatus)
						|| UserStatusEnum.cancelled.getValue().equals(previousStatus)) {
					this.service.promoteToMemberOrg(entity.getId(), entity.getDomainId());
				}
			}
		}
		finally {
			this.previousUserStatusHolder.remove();
		}
	}

	@Override
	protected void preDelete(WoUser entity) {
		if (entity != null && entity.getId() != null) {
			WoUser existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), existing.getId());
				this.assertDeletableSystemUser(java.util.Collections.singletonList(existing.getId()));
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		if (ids == null) {
			return;
		}
		List<Long> targetIds = new java.util.ArrayList<>();
		for (Object raw : ids) {
			WoUser existing = this.service.findById(Long.parseLong(raw.toString()));
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), existing.getId());
				targetIds.add(existing.getId());
			}
		}
		this.assertDeletableSystemUser(targetIds);
	}

	/**
	 * 系统种子用户（id=1）禁止删除；且不能删到超级管理员组（admin）只剩 0 人，
	 * 防止平台失去任何具备超管身份的账号。
	 */
	private void assertDeletableSystemUser(List<Long> targetIds) {
		if (targetIds.contains(Constants.SYSTEM_USER_ID)) {
			throw new BaseException("系统种子用户禁止删除");
		}
		List<Long> superAdmins = this.commonOperate.listSuperAdmin();
		long remaining = superAdmins.stream().filter(id -> !targetIds.contains(id)).count();
		if (!superAdmins.isEmpty() && remaining < 1) {
			throw new BaseException("必须保留至少一名超级管理员，不能删除最后一个超级管理员账号");
		}
		List<Long> tenantAdmins = this.commonOperate.listTenantAdmin();
		long remainingTenant = tenantAdmins.stream().filter(id -> !targetIds.contains(id)).count();
		if (!tenantAdmins.isEmpty() && remainingTenant < 1) {
			throw new BaseException("必须保留至少一名租户管理员，不能删除最后一个租户管理员账号");
		}
	}

	@ApiOperation(value = "管理工作组织列表", notes = "已废弃：现网 sys 后台不再使用工作组织切换")
	@Deprecated
	@GetMapping("workOrgs")
	public Result<List<AdminWorkOrgVO>> listWorkOrgs() {
		return Result.ok(java.util.Collections.emptyList());
	}

	@ApiOperation(value = "切换管理工作组织", notes = "已废弃：现网 sys 后台不再使用工作组织切换")
	@Deprecated
	@PostMapping("workOrg")
	public Result<Boolean> switchWorkOrg(@RequestBody Map<String, Object> body) {
		return Result.ok(Boolean.TRUE);
	}

	private void applyAdminUserListScope(PageQuery pageQuery) {
		this.applyAdminUserListScope(pageQuery, null, null);
	}

	/**
	 * @param targetDomainId 目标站点 ID（如：正在为哪个站点配置管理员候选人），与当前访问域 {@link #getDomainId()} 无关；
	 *                        为空时按既有逻辑回退（兼容未显式传入目标站点的调用方）。
	 * @param candidateProfile 候选人场景：{@code authCoverage} 权限覆盖核对；{@code orgMember} 组织/分群加成员（按租户成员池，不按 {@code wo_user.domain_id} 收窄）
	 */
	private void applyAdminUserListScope(PageQuery pageQuery, Long targetDomainId, String candidateProfile) {
		// domainId 是"候选人范围锚点"（谁能被选中，由操作者层级决定），不是"查询条件"（客户端想搜什么）；
		// 两者不能共用 condition 这个 key 命名空间。调用方（listSelect）已从原始请求参数里把 domainId 摘出，
		// 只以 targetDomainId 方法参数形式传入，这里防御性再摘一次，确保不管上游是否遗漏摘除，本方法
		// 的每个分支都只会显式 pushParam 回服务端算出的范围值，绝不会残留客户端同名原始值。
		pageQuery.removeParam(Constants.COMMON_KEY_DOMAIN_COLUMN);
		Long userId = this.getUserId();
		if (userId != null && this.commonOperate.isAdmin(userId)) {
			return;
		}
		if (userId != null && !"authCoverage".equals(candidateProfile)) {
			// 下级看不到上级：身份组 org_code + 登记册 user_id 双通道（tadmin 等可能不在平台 org 树）
			DataScopeQueryHelper.applyUpperIdentityExclusion(pageQuery, this.commonOperate.isTenantAdmin(userId)
					? Collections.singletonList(Constants.AdminOrgCode)
					: Arrays.asList(Constants.AdminOrgCode, Constants.TAdminOrgCode));
			java.util.List<Long> upperRegistryUserIds = new java.util.ArrayList<>();
			upperRegistryUserIds.addAll(this.commonOperate.listSuperAdmin());
			if (!this.commonOperate.isTenantAdmin(userId)) {
				upperRegistryUserIds.addAll(this.commonOperate.listTenantAdmin());
			}
			DataScopeQueryHelper.applyUpperIdentityUserIdExclusion(pageQuery, upperRegistryUserIds);
		}
		if (userId != null && this.commonOperate.isTenantAdmin(userId)) {
			this.applyScope(pageQuery, DataScopeProfile.ADMIN_TENANT, WoUser.class);
			return;
		}
		if (targetDomainId != null && userId != null && this.commonOperate.isDomainAdmin(userId)) {
			// 候选人范围锚定到目标站点本身，不依赖当前浏览器访问的域名；越权访问未绑定站点直接拒绝
			this.assertBoundDomainAccess(targetDomainId);
			if ("authCoverage".equals(candidateProfile)) {
				Long scopeComId = this.resolveCandidateScopeComId(targetDomainId, null);
				DataScopeQueryHelper.applyAuthCoverageCandidateScope(pageQuery, targetDomainId, scopeComId);
				return;
			}
			if ("orgMember".equals(candidateProfile)) {
				Long scopeComId = this.resolveCandidateScopeComId(targetDomainId, null);
				DataScopeQueryHelper.applyUserTenantCandidateScope(pageQuery, scopeComId);
				return;
			}
			pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, targetDomainId);
			return;
		}
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, WoUser.class);
	}

	private void rewriteOperationalOrgIdForMemberList(PageQuery pageQuery, Long domainId, Long requestComId) {
		Object rawOrgId = pageQuery.getCondition().get("orgId");
		if (rawOrgId == null || ObjectUtils.isBlank(rawOrgId.toString())) {
			return;
		}
		long orgId;
		try {
			orgId = Long.parseLong(rawOrgId.toString().trim());
		}
		catch (NumberFormatException ex) {
			return;
		}
		Long resolvedId = this.resolveOperationalOrgId(orgId, domainId, requestComId);
		if (resolvedId != null && !resolvedId.equals(orgId)) {
			pageQuery.pushParam("orgId", resolvedId);
		}
	}

	private Long resolveOperationalOrgId(Long orgId, Long domainId, Long requestComId) {
		if (orgId == null) {
			return null;
		}
		WoOrg org = this.orgService.findById(orgId);
		if (org == null || !this.orgOperationalKindBridge.isOperationalKind(org)) {
			return orgId;
		}
		if (org.getDomainId() != null && org.getDomainId() > 0) {
			return orgId;
		}
		if (domainId == null || domainId <= 0) {
			return orgId;
		}
		Long scopeComId = requestComId;
		if (scopeComId == null || scopeComId <= 0) {
			scopeComId = this.orgOperationalKindBridge.resolveComIdForDomain(domainId, this.getTenantId());
		}
		WoOrg resolved = this.orgOperationalKindBridge.resolveOperationalInstance(org, domainId, scopeComId);
		return resolved.getId() != null ? resolved.getId() : orgId;
	}

	/**
	 * 可管理的用户列表
	 *
	 * @param params 查询参数
	 * @return 用户列表
	 */
	@ApiOperation(value = "用户列表", notes = "可管理的用户列表，附带用户所属公司/组织，支持按组织筛选")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "email", value = "邮箱（模糊查询）", dataTypeClass = String.class, paramType = "query"),
			@ApiImplicitParam(name = "filterOrgId", value = "按所属组织过滤", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管跨租户视图）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（超管跨站点视图）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<UserOrgVO> listQuery(
			@ApiParam(value = "按所属组织过滤") @RequestParam(required = false) Long filterOrgId,
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId,
			@RequestParam Map<String, Object> params) {
		params.remove("filterOrgId");
		params.remove("comId");
		params.remove("domainId");
		PageQuery pageQuery = new PageQuery(params);

		this.applyAdminUserListScope(pageQuery);
		Long userId = this.getUserId();
		if (userId != null && this.commonOperate.isAdmin(userId)) {
			if (comId != null && Objects.equals(comId, Constants.TOP_COM_ID)
					&& (domainId == null || domainId <= 0)) {
				DataScopeQueryHelper.applyUserAdminPlatformMainComRead(pageQuery);
			}
			else if (comId != null && comId > 0) {
				pageQuery.pushParam("mainComId", comId);
			}
			if (domainId != null && domainId > 0) {
				pageQuery.removeParam(Constants.COMMON_KEY_DOMAIN_COLUMN);
				pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);
			}
		}
		DataScopeQueryHelper.applyOrgMemberCandidateScope(pageQuery, filterOrgId);

		PageData<UserOrgVO> page = this.service.queryUserWithOrgForPage(pageQuery);
		this.decorateWritable(page.getRows());
		return page;
	}

	/** 按行计算 writable：上级已在列表 SQL 排除；平级与下级可写，仅身份高于操作者的行不可写。 */
	private void decorateWritable(List<UserOrgVO> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}
		Long operatorId = this.getUserId();
		if (operatorId == null) {
			rows.forEach(row -> row.setWritable(Boolean.FALSE));
			return;
		}
		if (this.isAdmin(operatorId)) {
			rows.forEach(row -> row.setWritable(Boolean.TRUE));
			return;
		}
		List<Long> ids = rows.stream().map(UserOrgVO::getId).filter(id -> id != null).collect(Collectors.toList());
		Map<Long, AdminScopeLevel> levels = this.adminScopeService.resolveIdentityLevels(ids);
		AdminScopeLevel operator = this.adminScopeService.resolveLevel(operatorId);
		for (UserOrgVO row : rows) {
			boolean self = operatorId.equals(row.getId());
			AdminScopeLevel target = levels.getOrDefault(row.getId(), AdminScopeLevel.MEMBER);
			row.setWritable(self || operator.getRank() >= target.getRank());
		}
	}

	/**
	 * 查询组织下的用户列表
	 *
	 * @param params 查询参数
	 * @return 用户列表
	 */
	@ApiOperation(value = "组织用户列表", notes = "查询组织下的用户列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
	@ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（按站解析会员分群实例）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("org")
	public PageData<WoUser> listOrgUser(
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId,
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@RequestParam Map<String, Object> params) {
		params.remove("domainId");
		params.remove("comId");
		PageQuery pageQuery = new PageQuery(params);

		this.applyAdminUserListScope(pageQuery);
		this.rewriteOperationalOrgIdForMemberList(pageQuery, domainId, comId);

		return this.service.queryUserForPage(new WoUser(), pageQuery);
	}

	/**
	 * 查询租户下的管理员列表
	 *
	 * @param params 查询参数,根据uComId(本租户id)、orgId(租户管理系统用户组,隶属于comId=0的平台)查询租户管理用户组下归属本租户的人员（即本租户的管理员）
	 * @return 租户管理员列表
	 */
	@ApiOperation(value = "租户管理员列表", notes = "查询租户下的管理员列表，根据uComId(本租户id)、orgId(租户管理系统用户组)查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "uComId", value = "用户公司ID（本租户id）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "userComId", value = "本租户id（与 uComId 同义，兼容前端）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("com")
	public PageData<WoUser> listComAdminUser(@RequestParam Map<String, Object> params) {
		Long comId = this.resolveTenantAdminListComId(params);
		params.remove("userComId");
		params.remove("uComId");
		PageQuery pageQuery = new PageQuery(params);
		this.applyAdminUserListScope(pageQuery);
		return this.service.listTenantAdminsForCompany(comId, pageQuery);
	}

	private Long resolveTenantAdminListComId(Map<String, Object> params) {
		Object raw = params != null ? params.get("userComId") : null;
		if (raw == null && params != null) {
			raw = params.get("uComId");
		}
		if (raw == null || ObjectUtils.isBlank(ObjectUtils.string(raw))) {
			throw new BaseException("须指定租户（userComId）", ResultCode.VALIDATION_ERROR.getCode());
		}
		return Long.parseLong(ObjectUtils.string(raw));
	}

	/**
	 * 可添加的用户列表
	 *
	 * @param params 查询参数
	 * @return 可添加用户
	 */
	@ApiOperation(value = "可选用户列表", notes = "可添加的用户列表；domainId 用于站点管理员配置候选人时锚定目标站点，与当前访问域无关")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "username", value = "用户名（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点ID（站点管理员配置候选人时传入，锚定目标站点而非当前访问域；同时用于服务端解析站点归属租户，覆盖 scopeComId）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "scopeComId", value = "候选人所属租户（仅 wo_com_user 成员；domainId 有值时以站点归属租户覆盖）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "filterOrgId", value = "候选人所属组织过滤（按组织缩小候选人范围）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "candidateProfile", value = "候选人场景：authCoverage=权限覆盖核对；orgMember=组织/分群加成员", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "excludeOrgId", value = "排除已在该组织内的用户（加成员候选人）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("select")
	public PageData<WoUser> listSelect(
			@ApiParam(value = "目标站点ID") @RequestParam(required = false) Long domainId,
			@ApiParam(value = "候选人所属租户范围") @RequestParam(required = false) Long scopeComId,
			@ApiParam(value = "候选人所属组织过滤") @RequestParam(required = false) Long filterOrgId,
			@ApiParam(value = "候选人场景") @RequestParam(required = false) String candidateProfile,
			@ApiParam(value = "排除已在组织内的用户") @RequestParam(required = false) Long excludeOrgId,
			@RequestParam Map<String, Object> params) {
		// domainId、scopeComId、filterOrgId 均为独立的候选人范围锚点参数，语义是"限定候选池"而非"业务查询条件"；
		// Spring 会把它们同时绑进这个 Map，必须在构造 PageQuery 之前摘除，避免以查询条件的身份
		// 混入 condition——范围锚点只能由 applyAdminUserListScope / applyUserTenantCandidateScope / applyOrgMemberCandidateScope 按操作者层级显式写回。
		params.remove("domainId");
		params.remove("scopeComId");
		params.remove("filterOrgId");
		params.remove("candidateProfile");
		params.remove("excludeOrgId");
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		this.applyAdminUserListScope(pageQuery, domainId, candidateProfile);
		Long selectScopeComId = this.resolveSelectScopeComId(domainId, scopeComId, candidateProfile);
		DataScopeQueryHelper.applyUserTenantCandidateScope(pageQuery, selectScopeComId);
		DataScopeQueryHelper.applyOrgMemberCandidateScope(pageQuery, filterOrgId);
		Long resolvedExcludeOrgId = this.resolveOperationalOrgId(excludeOrgId, domainId, selectScopeComId);
		DataScopeQueryHelper.applyOrgMemberExcludeScope(pageQuery, resolvedExcludeOrgId);

		return this.service.execQueryForPage(new WoUser(), pageQuery);
	}

	/**
	 * 可选用户列表的租户成员池：超管在组织/分群加成员时，{@code domainId} 只用于解析分群实例与写侧校验，
	 * 不再用站点归属租户强制 {@code wo_com_user} 收窄；仅当工具栏/组织明确传入 {@code scopeComId} 时才按租户过滤。
	 */
	private Long resolveSelectScopeComId(Long domainId, Long scopeComId, String candidateProfile) {
		Long userId = this.getUserId();
		if ("orgMember".equals(candidateProfile) && userId != null && this.commonOperate.isAdmin(userId)) {
			if (scopeComId == null || scopeComId <= 0 || Objects.equals(scopeComId, Constants.TOP_COM_ID)) {
				return null;
			}
			return scopeComId;
		}
		return this.resolveCandidateScopeComId(domainId, scopeComId);
	}

	/**
	 * 候选人所属租户范围解析：domainId 有值时（站点管理员配置候选人）以站点归属租户（{@code wo_domain.comId}）为准，
	 * 服务端查库覆盖，不信任前端直传；否则使用调用方直传的 scopeComId（添加租户管理员、组织加成员场景）。
	 */
	private Long resolveCandidateScopeComId(Long domainId, Long scopeComId) {
		if (domainId == null) {
			return scopeComId;
		}
		WoDomain domain = this.domainDao.findById(domainId).orElse(null);
		return domain != null ? domain.getComId() : null;
	}

	/**
	 * 设置用户主组织：用户管理"配置"表单修改组织时调用，只迁移该用户主组织记录，不影响其它兼职组织归属。
	 */
	@ApiOperation(value = "设置用户主组织", notes = "只调整该用户主组织这一条 wo_org_user 记录的所属组织，其它兼职组织记录不受影响")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = Long.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "orgId", value = "目标组织ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@PostMapping("org/primary")
	@OpLog(action = "设置用户主组织", resourceType = "user", resourceId = "#body['userId']")
	public Result<Boolean> setPrimaryOrg(@RequestBody Map<String, Object> body) {
		Long userId = Long.parseLong(body.get("userId").toString());
		Long orgId = Long.parseLong(body.get("orgId").toString());
		WoUser existing = this.service.findById(userId);
		if (existing != null) {
			this.assertEntityRowScope(existing);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), existing.getId());
		}
		this.service.setUserPrimaryOrg(userId, orgId, this.getUserId(), this.getUserIp());
		return Result.ok(Boolean.TRUE);
	}

	/**
	 * 设置用户主企业：用户管理"配置"表单修改公司时调用。非超管只能把用户主企业设为自己所在租户，
	 * 不能跨租户改绑；超管可指定任意目标公司。
	 */
	@ApiOperation(value = "设置用户主企业", notes = "只调整该用户主企业这一条 wo_com_user 记录的所属公司，其它兼职企业关联不受影响；非超管仅可设为自身所在租户")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = Long.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "comId", value = "目标公司ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@PostMapping("com/primary")
	@OpLog(action = "设置用户主企业", resourceType = "user", resourceId = "#body['userId']", targetComId = "#body['comId']")
	public Result<Boolean> setPrimaryCompany(@RequestBody Map<String, Object> body) {
		Long userId = Long.parseLong(body.get("userId").toString());
		Long comId = Long.parseLong(body.get("comId").toString());
		if (!this.isAdmin(this.getUserId()) && !comId.equals(this.getTenantId())) {
			throw new BaseException("无权跨租户设置主企业");
		}
		WoUser existing = this.service.findById(userId);
		if (existing != null) {
			this.assertEntityRowScope(existing);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), existing.getId());
		}
		this.service.setUserPrimaryCompany(userId, comId, this.getUserId(), this.getUserIp());
		return Result.ok(Boolean.TRUE);
	}

	/**
	 * 获取当前用户有权限的管理菜单
	 *
	 * @return 管理菜单
	 */
	@ApiOperation(value = "当前用户管理菜单", notes = "获取当前用户有权限的管理菜单")
	@GetMapping("adminMenu")
	public List<Menu> adminMenu() {
		return this.service.queryAdminMenuByUser(this.getDomainId(), this.getTenantId(), this.getUserId());
	}

	/**
	 * 当前用户管理端操作权限（admin_button resourceCode 列表；超管为 ["*"]）。
	 */
	@ApiOperation(value = "当前用户管理端操作权限", notes = "admin_button/admin_plugin_button 的 resourceCode 列表，与门户 currentAuthority 同语义")
	@GetMapping("adminAuthorities")
	public Result<List<String>> adminAuthorities() {
		return Result.ok(this.service.queryAdminAuthorityByUser(this.getDomainId(), this.getTenantId(), this.getUserId()));
	}

	/**
	 * 管理端顶栏「使用文档」开关与链接（优先域级 wo_domain_option，再回退 wo_options）。
	 */
	@ApiOperation(value = "管理端使用文档配置", notes = "按当前访问域解析 admin_header_help_enabled / admin_header_help_url")
	@GetMapping("adminHeaderHelp")
	public Result<AdminHeaderHelpConfig> adminHeaderHelp() {
		return Result.ok(this.optionsService.findAdminHeaderHelp(this.getDomainId()));
	}

	/**
	 * 管理端首页（/admin Tab）：本域 → 根域 → 全局 → builtin。可选 domainId 供域级参数页预览。
	 */
	@ApiOperation(value = "管理端首页配置", notes = "按访问域四级兜底解析 admin_home_config；可选 domainId 预览指定站点")
	@GetMapping("adminHome")
	public Result<AdminHomeConfigVO> adminHome(
			@ApiParam(value = "预览用站点 ID，缺省为当前访问域") @RequestParam(required = false) Long domainId) {
		Long resolveId = domainId != null ? domainId : this.getDomainId();
		return Result.ok(this.optionsService.findAdminHomeConfig(resolveId));
	}

	/**
	 * 获取指定用户有权限的管理菜单
	 *
	 * @return 管理菜单
	 */
	@ApiOperation(value = "指定用户管理菜单", notes = "获取指定用户有权限的管理菜单")
	@GetMapping("adminMenu/{userId}")
	public List<Menu> adminMenuByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
		WoUser target = this.service.findById(userId);
		Long operatorId = this.getUserId();
		if (target != null && (operatorId == null || !userId.equals(operatorId))) {
			this.assertEntityRowScope(target);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(operatorId, target.getId());
		}
		UserAuthContext authContext = this.service.resolveUserAuthContext(userId, this.getDomainId(), this.getTenantId());
		return this.service.queryAdminMenuByUser(authContext.getDomainId(), authContext.getComId(), userId);
	}

	@ApiOperation(value = "用户归属组织与角色", notes = "权限查看「归属与角色」页签：用户归属的组织列表及每个组织授予的角色")
	@GetMapping("orgRoles/{userId}")
	public Result<List<UserOrgRoleVO>> orgRolesByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
		WoUser target = this.service.findById(userId);
		Long operatorId = this.getUserId();
		if (target != null && (operatorId == null || !userId.equals(operatorId))) {
			this.assertEntityRowScope(target);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(operatorId, target.getId());
		}
		return Result.ok(this.service.queryUserOrgRoles(userId));
	}

	@ApiOperation(value = "用户角色四来源", notes = "权限查看「角色来源」：组织角色 + 直挂角色 + 主体角色 + 身份角色")
	@GetMapping("role/list")
	public Result<UserRoleSourceVO> roleSourcesByUserId(@ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
		WoUser target = this.service.findById(userId);
		Long operatorId = this.getUserId();
		if (target != null && (operatorId == null || !userId.equals(operatorId))) {
			this.assertEntityRowScope(target);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(operatorId, target.getId());
		}
		return Result.ok(this.service.queryUserRoleSources(userId));
	}

	@ApiOperation(value = "用户直挂角色", notes = "人直挂角色（写 wo_role_binding target_type=user），受身份层级与可授范围约束")
	@PostMapping("role/bind")
	public Result bindUserRole(@Valid @RequestBody UserRoleBindBody body) {
		this.roleBindingService.bindUserRole(body.getUserId(), body.getRoleId());
		return Result.ok();
	}

	@ApiOperation(value = "用户解绑角色", notes = "解绑直挂角色（软删绑定行）")
	@PostMapping("role/unbind")
	public Result unbindUserRole(@Valid @RequestBody UserRoleBindBody body) {
		this.roleBindingService.unbindUserRole(body.getUserId(), body.getRoleId());
		return Result.ok();
	}

	/**
	 * 管理端查看指定用户的门户权限（按目标用户域/租户解析）。
	 */
	@ApiOperation(value = "指定用户门户权限", notes = "管理端查看指定用户的门户菜单与按钮权限")
	@GetMapping("menu/{userId}")
	public UserAuth adminUserAuthByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
		WoUser target = this.service.findById(userId);
		Long operatorId = this.getUserId();
		if (target != null && (operatorId == null || !userId.equals(operatorId))) {
			this.assertEntityRowScope(target);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(operatorId, target.getId());
		}
		return this.service.queryUserAuth(this.getDomainId(), this.request, this.getTenantId(), userId);
	}

	/**
	 * 管理员后台设置新密码，直接覆盖原密码
	 *
	 * @param adminPasswdModifyParams 管理员密码修改参数（不需要原密码）
	 * @return 密码修改结果
	 */
	@ApiOperation(value = "管理员修改密码", notes = "管理员后台设置新密码，直接覆盖原密码")
	@PostMapping("passwd4admin")
	@OpLog(action = "管理员重置密码", resourceType = "user", resourceId = "#adminPasswdModifyParams.id",
			sensitiveFields = {"password", "passwd", "newPasswd", "oldPasswd", "confirm", "token", "secret", "captcha"})
	public Login changePasswd4admin(@ApiParam(value = "管理员密码修改参数", required = true) @Valid @RequestBody AdminPasswdModifyParams adminPasswdModifyParams) {

		WoUser target = this.service.findById(adminPasswdModifyParams.getId());
		if (target != null) {
			this.assertEntityRowScope(target);
			this.adminScopeService.assertTargetIdentityLowerThanOperator(this.getUserId(), target.getId());
		}
		getLog().info("用户id: {} 密码修改, 修改人id：{}", adminPasswdModifyParams.getId(), this.getUserId());
		// 转换为 PasswdModifyParams（不包含 oldPasswd）
		PasswdModifyParams passwdModifyParams = new PasswdModifyParams();
		passwdModifyParams.setId(adminPasswdModifyParams.getId());
		passwdModifyParams.setPassword(adminPasswdModifyParams.getPassword());
		passwdModifyParams.setConfirm(adminPasswdModifyParams.getConfirm());
		
		Login user = this.loginAuthService.changePasswd4admin(passwdModifyParams);
		if (user == null) {
			getLog().warn("{} 密码修改失败", adminPasswdModifyParams.getId());
			user = new Login();
			user.setStatus("error");
			user.setNews("密码修改失败，请重试！");
		}
		return user;
	}

	/**
	 * 管理员后台添加新用户
	 * 租户管理员添加后，还需要从组织管理添加人员到组织，才能在用户管理看到用户
	 *
	 * @param register 用户注册信息
	 * @return 注册结果
	 */
	@ApiOperation(value = "管理员添加用户", notes = "管理员后台添加新用户")
	@PostMapping("register4admin")
	@OpLog(action = "管理员新增用户", resourceType = "user", resourceId = "#register.id",
			targetComId = "#register.comId != null ? #register.comId.toString() : ''")
	public Login addUser4admin(@ApiParam(value = "用户注册信息", required = true) @Valid @RequestBody Register register) {
		register.setId(this.nextId());
		register.setRegisterIp(this.getUserIp());
		this.normalizeRegisterComIdFromOrg(register);
		this.normalizeRegisterScopeForAdmin(register);
		String loginName = ObjectUtils.string(register.getLoginName()).trim();
		if (!ObjectUtils.isBlank(loginName)) {
			register.setLoginName(loginName);
		}
		else if (!ObjectUtils.isBlank(register.getEmail())) {
			register.setLoginName(register.getEmail().trim());
		}
		getLog().info("register loginName={}", register.getLoginName());
		Long registerDomainId = this.resolveRegisterDomainId(register);
		return this.loginAuthService.addUser4admin(registerDomainId, this.getTenantId(), this.getUserId(), register, false);
	}

	private Long resolveRegisterDomainId(Register register) {
		if (register != null && register.getDomainId() != null && register.getDomainId() > 0) {
			return register.getDomainId();
		}
		if (register != null && register.getOrgId() != null && register.getOrgId() > 0) {
			WoOrg org = this.orgService.findById(register.getOrgId());
			if (org != null && org.getDomainId() != null && org.getDomainId() > 0) {
				return org.getDomainId();
			}
		}
		return this.getDomainId();
	}

	private void normalizeRegisterScopeForAdmin(Register register) {
		if (register == null || !this.isAdmin(this.getUserId())) {
			return;
		}
		Long domainId = register.getDomainId();
		if (domainId == null || domainId <= 0) {
			if (register.getOrgId() != null && register.getOrgId() > 0) {
				WoOrg org = this.orgService.findById(register.getOrgId());
				if (org != null && org.getDomainId() != null && org.getDomainId() > 0) {
					register.setDomainId(org.getDomainId());
					domainId = org.getDomainId();
				}
			}
		}
		if (domainId == null || domainId <= 0) {
			return;
		}
		Long resolvedComId = this.domainDao.findById(domainId).map(WoDomain::getComId).orElse(null);
		AdminWriteScopeStamper.ensureComIdFromDomain(register, domainId, resolvedComId);
	}

	private void normalizeRegisterComIdFromOrg(Register register) {
		if (register == null || register.getOrgId() == null || register.getOrgId() <= 0) {
			return;
		}
		Long requestComId = register.getComId();
		if (requestComId != null && requestComId > 0 && !Objects.equals(requestComId, Constants.TOP_COM_ID)) {
			return;
		}
		WoOrg org = this.orgService.findById(register.getOrgId());
		if (org == null || org.getComId() == null || org.getComId() <= 0
				|| Objects.equals(org.getComId(), Constants.TOP_COM_ID)) {
			return;
		}
		register.setComId(org.getComId());
		if (register.getArchId() == null && org.getArchId() != null) {
			register.setArchId(org.getArchId());
		}
	}
}