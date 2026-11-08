/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;

import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.entity.WoSubject;
import com.wldos.platform.core.service.SubjectAdminService;
import com.wldos.platform.core.vo.SubjectAuthBody;
import com.wldos.platform.core.vo.SubjectAuthVO;
import com.wldos.platform.core.vo.SubjectGrantBody;
import com.wldos.platform.core.vo.SubjectGrantVO;

import io.github.wldos.common.res.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主体管理：主体类型字典、主体认证（用户↔主体类型）、主体授权（主体类型↔角色）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "主体管理")
@RestController
@RequestMapping("admin/sys/subject")
public class SubjectAdminController extends NonEntityController<SubjectAdminService> {

	@ApiOperation("主体类型字典")
	@GetMapping("types")
	public Result<List<WoSubject>> types() {
		return Result.ok(this.service.listTypes());
	}

	@ApiOperation("主体认证清单")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "comId", value = "租户id（超管可指定，缺省平台；租户管理员固定本租户）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点（多站时工具栏筛选；租户级行在各站视图均可见）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("auth-list")
	public Result<List<SubjectAuthVO>> authList(
			@RequestParam(value = "comId", required = false) Long comId,
			@RequestParam(value = "domainId", required = false) Long domainId) {
		return Result.ok(this.service.listAuthentications(comId, domainId));
	}

	@ApiOperation("添加主体认证（用户挂主体类型）")
	@PostMapping("auth-add")
	public Result<String> authAdd(@ApiParam("认证参数") @RequestBody SubjectAuthBody body) {
		this.service.addAuthentication(body);
		return Result.ok("添加成功");
	}

	@ApiOperation("移除主体认证")
	@PostMapping("auth-remove")
	public Result<String> authRemove(@ApiParam("移除参数") @RequestBody SubjectAuthBody body) {
		this.service.removeAuthentication(body != null ? body.getId() : null);
		return Result.ok("已移除");
	}

	@ApiOperation("主体授权清单")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "comId", value = "租户id（超管可指定，缺省平台；租户管理员固定本租户）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点（多站时工具栏筛选；租户级行在各站视图均可见）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("grant-list")
	public Result<List<SubjectGrantVO>> grantList(
			@RequestParam(value = "comId", required = false) Long comId,
			@RequestParam(value = "domainId", required = false) Long domainId) {
		return Result.ok(this.service.listAssociations(comId, domainId));
	}

	@ApiOperation("添加主体授权（主体类型挂角色）")
	@PostMapping("grant-add")
	public Result<String> grantAdd(@ApiParam("授权参数") @RequestBody SubjectGrantBody body) {
		this.service.addAssociation(body);
		return Result.ok("添加成功");
	}

	@ApiOperation("移除主体授权")
	@PostMapping("grant-remove")
	public Result<String> grantRemove(@ApiParam("移除参数") @RequestBody SubjectGrantBody body) {
		this.service.removeAssociation(body != null ? body.getId() : null);
		return Result.ok("已移除");
	}
}
