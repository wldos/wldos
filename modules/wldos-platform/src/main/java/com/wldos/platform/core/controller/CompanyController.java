/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.vo.SelectOption;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.service.CompanyService;
import com.wldos.platform.core.service.PlatformOperatorSettingsService;
import com.wldos.platform.core.service.TenantProvisionService;
import com.wldos.platform.core.vo.TenantProvisionBody;
import com.wldos.platform.core.vo.TenantProvisionResult;

import com.wldos.platform.core.vo.Company;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.context.annotation.Lazy;
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
@Api(tags = "公司管理")
@SuppressWarnings({ "unchecked" })
@RestController
@RequestMapping("admin/sys/com")
public class CompanyController extends EntityController<CompanyService, WoCompany> {

	private final TenantProvisionService tenantProvisionService;

	private final PlatformOperatorSettingsService platformOperatorSettingsService;

	public CompanyController(
			TenantProvisionService tenantProvisionService,
			@Lazy PlatformOperatorSettingsService platformOperatorSettingsService) {
		this.tenantProvisionService = tenantProvisionService;
		this.platformOperatorSettingsService = platformOperatorSettingsService;
	}
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params 查询条件
	 * @return 公司列表
	 */
	@ApiOperation(value = "公司列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "comName", value = "公司名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageData<Company> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_TENANT, WoCompany.class);
		DataScopeQueryHelper.remapTenantComIdToPrimaryId(pageQuery);
		PageData<Company> pageData = this.service.execQueryForTree(new Company(), new WoCompany(), pageQuery, Constants.TOP_COM_ID);
		this.platformOperatorSettingsService.applyTenantPickerVisibilityOnTree(pageData.getRows(), this.getUserId());
		CompanyService.appendVirtualPlatformCompanyRootIfNeeded(pageData.getRows(), this.showVirtualPlatformCompanyCatalog());
		return pageData;
	}

	@GetMapping("get")
	@Override
	public Object getEntityById(@RequestParam long id) {
		WoCompany company = this.service.findById(id);
		if (company != null) {
			this.assertEntityRowScope(company);
		}
		return company;
	}

	@Override
	protected void preAdd(WoCompany entity) {
		this.assertNotSiteOnlyAdminForWrite();
		this.assertSuperAdminOnlyForCompanyProvisioning();
	}

	@Override
	protected void preUpdate(WoCompany entity) {
		this.assertNotSiteOnlyAdminForWrite();
		if (entity != null && entity.getId() != null) {
			WoCompany existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
			}
		}
	}

	@Override
	protected void preDelete(WoCompany entity) {
		this.assertNotSiteOnlyAdminForWrite();
		this.assertSuperAdminOnlyForCompanyProvisioning();
		if (entity != null && entity.getId() != null) {
			if (Objects.equals(entity.getId(), Constants.TOP_COM_ID)) {
				throw new BaseException("「平台」为虚拟树顶（id=0），不可删除", ResultCode.OPERATION_FAILED.getCode());
			}
			WoCompany existing = this.service.findById(entity.getId());
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.service.assertDeletable(existing);
			}
		}
	}

	@Override
	protected void preDeletes(List<Object> ids) {
		this.assertNotSiteOnlyAdminForWrite();
		this.assertSuperAdminOnlyForCompanyProvisioning();
		if (ids == null) {
			return;
		}
		for (Object raw : ids) {
			long id = Long.parseLong(raw.toString());
			if (Objects.equals(id, Constants.TOP_COM_ID)) {
				throw new BaseException("「平台」为虚拟树顶（id=0），不可删除", ResultCode.OPERATION_FAILED.getCode());
			}
			WoCompany existing = this.service.findById(id);
			if (existing != null) {
				this.assertEntityRowScope(existing);
				this.service.assertDeletable(existing);
			}
		}
	}

	/**
	 * 支持多租户查询的公司下拉选项列表
	 */
	@ApiOperation(value = "公司下拉选项", notes = "客户租户代管/开通下拉默认排除 platform-operator；超管与平台主站站点管理员自动包含运营主体（展示名称与 scope）")
	@GetMapping("select")
	public List<SelectOption> queryComSelectOption() {
		List<WoCompany> companies = this.allEntities();
		return companies.stream()
				.map(com -> SelectOption.of(com.getComName(), com.getId().toString()))
				.collect(Collectors.toList());
	}

	/**
	 * 超级管理员后台给租户添加管理员。
	 *
	 * @param orgUser 添加人员参数
	 * @return 添加结果
	 */
	@ApiOperation(value = "添加租户管理员", notes = "超级管理员后台给租户添加管理员")
	@PostMapping("admin")
	@OpLog(action = "添加租户管理员", resourceType = "company", resourceId = "#orgUser['userComId']")
	public Result addTenantAdmin(@ApiParam(value = "添加人员参数", required = true) @RequestBody Map<String, Object> orgUser) {
		Long userComId = Long.parseLong(orgUser.get("userComId").toString());
		List<String> userIds = (List<String>) orgUser.get("userIds");
		Long curUserId = this.getUserId();
		String uip = this.getUserIp();

		String message = this.service.addTenantAdmin(userIds, userComId, curUserId, uip);

		return Result.ok(message);
	}

	@ApiOperation(value = "租户入驻", notes = "单事务创建公司、体系、主站、bootstrap 与租户管理员")
	@PostMapping("provision")
	@OpLog(action = "租户入驻", resourceType = "company")
	public Result<TenantProvisionResult> provisionTenant(
			@ApiParam(value = "入驻参数", required = true) @RequestBody TenantProvisionBody body) {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("仅平台超管可执行租户入驻", ResultCode.FORBIDDEN.getCode());
		}
		TenantProvisionResult result = this.tenantProvisionService.provision(body, this.getUserId(), this.getUserIp());
		this.refreshAuth();
		return Result.ok(result);
	}

	@ApiOperation(value = "移除租户管理员", notes = "移除租户管理员")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "用户ID列表", dataTypeClass = List.class, paramType = "body", required = true),
		@ApiImplicitParam(name = "userComId", value = "用户公司ID", dataTypeClass = Long.class, paramType = "body", required = true)
	})
	@DeleteMapping("rmAdmin")
	@OpLog(action = "移除租户管理员", resourceType = "company", resourceId = "#params['userComId']")
	public Boolean removeTenantAdmin(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<String>) params.get("ids")).stream().map(Long::parseLong).collect(Collectors.toList());
		Long userComId = Long.parseLong(ObjectUtils.string(params.get("userComId")));
		this.service.removeTenantAdmin(ids, userComId);

		return Boolean.TRUE;
	}

	/**
	 * 查询公司列表时追加平台根
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	@Override
	protected List<WoCompany> doFilter(List<WoCompany> res) {
		res = this.platformOperatorSettingsService.applyTenantPickerVisibility(res, this.getUserId());
		CompanyService.appendVirtualPlatformCompanyIfNeeded(res, this.showVirtualPlatformCompanyCatalog());
		return res;
	}

	private boolean showVirtualPlatformCompanyCatalog() {
		return this.isAdmin(this.getUserId()) || this.isPlatformAdmin(this.getTenantId());
	}

	private void assertNotSiteOnlyAdminForWrite() {
		Long userId = this.getUserId();
		if (userId == null || this.isAdmin(userId) || this.commonOperate.isTenantAdmin(userId)) {
			return;
		}
		if (this.commonOperate.isDomainAdmin(userId)) {
			throw new BaseException("站点管理员无权修改公司信息", ResultCode.FORBIDDEN.getCode());
		}
	}

	/** 新建/删除公司（租户）仅平台超管；租户管理员仅可改本租户公司配置。 */
	private void assertSuperAdminOnlyForCompanyProvisioning() {
		Long userId = this.getUserId();
		if (userId == null || this.isAdmin(userId)) {
			return;
		}
		if (this.commonOperate.isTenantAdmin(userId)) {
			throw new BaseException("租户管理员无权新建或删除公司", ResultCode.FORBIDDEN.getCode());
		}
	}

}