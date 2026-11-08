/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.service.CompanyService;
import com.wldos.platform.core.service.IdentityRegistryService;
import com.wldos.platform.core.vo.IdentityMemberBody;
import com.wldos.platform.core.vo.IdentityMemberVO;

import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.audit.annotation.OpLog;

import org.springframework.beans.factory.annotation.Autowired;
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
 * 身份登记册管理端：平台超管对 admin/tadmin/can_trust 身份的统一维护入口（dadmin 由站点管理绑定入口维护）。
 * 仅平台超管可用。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "身份登记册")
@RestController
@RequestMapping("admin/sys/identity")
public class IdentityRegistryAdminController extends NonEntityController<IdentityRegistryService> {

	@Autowired
	private CompanyService companyService;

	/** 本控制器全部端点仅平台超管可用。 */
	private void assertSuperAdmin() {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("仅平台超级管理员可用", ResultCode.FORBIDDEN.getCode());
		}
	}

	@ApiOperation(value = "登记册成员清单", notes = "按身份类别查询登记册成员；tadmin/dadmin 支持 comId 目标租户筛选（空=全部），dadmin 另支持 domainId")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "kind", value = "身份类别：admin/tadmin/dadmin/can_trust", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "comName", value = "租户名称模糊（tadmin 可选，与 comId 二选一）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 id（tadmin/dadmin；空=全部租户）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点id（dadmin 级联）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("registry")
	public Result<List<IdentityMemberVO>> registry(@RequestParam String kind,
			@RequestParam(required = false) String comName,
			@RequestParam(required = false) Long comId,
			@RequestParam(required = false) Long domainId) {
		this.assertSuperAdmin();
		return Result.ok(this.service.listMembers(kind, comName, comId, domainId));
	}

	@ApiOperation(value = "登记册添加成员", notes = "admin/can_trust 写登记册并投影身份组成员；tadmin 须指定受托租户")
	@PostMapping("add")
	@OpLog(action = "身份登记册添加成员", resourceType = "identity", resourceId = "#body.kind")
	public Result<String> add(@ApiParam(value = "成员入参", required = true) @RequestBody IdentityMemberBody body) {
		this.assertSuperAdmin();
		this.assertKindSupported(body.getKind());
		if (ObjectUtils.isBlank(body.getUserIds())) {
			throw new BaseException("用户id列表不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (Constants.TAdminOrgCode.equals(body.getKind())) {
			if (body.getUserComId() == null || body.getUserComId() <= 0) {
				throw new BaseException("添加租户管理员须指定受托租户（userComId）", ResultCode.VALIDATION_ERROR.getCode());
			}
			return Result.ok(this.companyService.addTenantAdmin(body.getUserIds(), body.getUserComId(), this.getUserId(), this.getUserIp()));
		}
		if (Constants.AdminOrgCode.equals(body.getKind())) {
			return Result.ok(this.service.addPlatformAdmin(body.getUserIds(), this.getUserId(), this.getUserIp()));
		}
		return Result.ok(this.service.addTrustMan(body.getUserIds(), this.getUserId(), this.getUserIp()));
	}

	@ApiOperation(value = "登记册移除成员", notes = "软删登记册并对称移除身份组成员；仅超管保底至少留一人，其余身份可为空")
	@PostMapping("remove")
	@OpLog(action = "身份登记册移除成员", resourceType = "identity", resourceId = "#body.kind")
	public Result<Boolean> remove(@ApiParam(value = "成员入参", required = true) @RequestBody IdentityMemberBody body) {
		this.assertSuperAdmin();
		this.assertKindSupported(body.getKind());
		if (ObjectUtils.isBlank(body.getUserIds())) {
			throw new BaseException("用户id列表不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		List<Long> ids = body.getUserIds().stream().map(Long::parseLong).collect(Collectors.toList());
		Long curUserId = this.getUserId();
		if (curUserId != null && ids.contains(curUserId)) {
			throw new BaseException("不能移除当前登录的管理员自身", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (Constants.TAdminOrgCode.equals(body.getKind())) {
			this.companyService.removeTenantAdmin(ids, null);
			return Result.ok(Boolean.TRUE);
		}
		if (Constants.AdminOrgCode.equals(body.getKind())) {
			this.service.removePlatformAdmin(ids, curUserId, this.getUserIp());
			return Result.ok(Boolean.TRUE);
		}
		this.service.removeTrustMan(ids, curUserId, this.getUserIp());
		return Result.ok(Boolean.TRUE);
	}

	private void assertKindSupported(String kind) {
		if (Constants.DomainAdminOrgCode.equals(kind)) {
			throw new BaseException("站点管理员请在站点管理中按站点绑定", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!Constants.AdminOrgCode.equals(kind) && !Constants.TAdminOrgCode.equals(kind)
				&& !Constants.CanTrustOrgCode.equals(kind)) {
			throw new BaseException("未知身份类别：" + kind, ResultCode.VALIDATION_ERROR.getCode());
		}
	}
}
