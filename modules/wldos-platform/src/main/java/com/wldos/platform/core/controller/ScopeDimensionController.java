/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.entity.WoScopeDimension;
import com.wldos.platform.core.service.AdminScopeService;
import com.wldos.platform.core.service.DictService;
import com.wldos.platform.core.service.RegionCascadeService;
import com.wldos.platform.core.service.RoleScopeService;
import com.wldos.platform.core.service.ScopeDictSourcesService;
import com.wldos.platform.core.service.ScopeDimensionOptionsService;
import com.wldos.platform.core.vo.RegionCascadeNode;
import com.wldos.platform.core.vo.RoleScopeDimensionsVO;
import com.wldos.platform.core.vo.ScopeDictSourceVO;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;

import io.github.wldos.common.vo.SelectOption;
import io.github.wldos.common.vo.TreeSelectOption;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "业务字段管理")
@RestController
@RequestMapping("/admin/sys/scope")
public class ScopeDimensionController extends NonEntityController<RoleScopeService> {

	private final ScopeDimensionOptionsService optionsService;
	private final RegionCascadeService regionCascadeService;
	private final ScopeDictSourcesService dictSourcesService;
	private final AdminScopeService adminScopeService;
	private final DictService dictService;

	public ScopeDimensionController(ScopeDimensionOptionsService optionsService,
			RegionCascadeService regionCascadeService,
			ScopeDictSourcesService dictSourcesService,
			AdminScopeService adminScopeService,
			DictService dictService) {
		this.optionsService = optionsService;
		this.regionCascadeService = regionCascadeService;
		this.dictSourcesService = dictSourcesService;
		this.adminScopeService = adminScopeService;
		this.dictService = dictService;
	}

	@ApiOperation("枚举类型字典源列表（登记业务字段用）")
	@GetMapping("dict-sources")
	public Result<List<ScopeDictSourceVO>> listDictSources() {
		this.assertPlatformAdmin();
		return Result.ok(this.dictSourcesService.listDictSources());
	}

	private void assertPlatformAdmin() {
		if (!this.isAdmin(this.getUserId())) {
			throw new BaseException("无权查询字典源", ResultCode.FORBIDDEN.getCode());
		}
	}

	@ApiOperation("已启用的业务字段列表")
	@GetMapping("dimensions")
	public Result<List<WoScopeDimension>> listDimensions() {
		return Result.ok(this.service.listEnabledDimensions());
	}

	@ApiOperation("角色管理范围维清单（全局字段 + 定制字段）")
	@GetMapping("dimensions/grantable")
	public Result<RoleScopeDimensionsVO> listGrantableDimensions(
			@ApiParam("编辑中的角色 id，用于回显已选用扩展维") @RequestParam(required = false) Long roleId) {
		return Result.ok(this.adminScopeService.grantableScopeDimensionsView(this.getUserId(), roleId));
	}

	@ApiOperation("角色可新选用的扩展枚举类型池")
	@GetMapping("dimensions/pickable")
	public Result<List<ScopeDictSourceVO>> listPickableDictTypes(
			@ApiParam("编辑中的角色 id") @RequestParam(required = false) Long roleId) {
		return Result.ok(this.adminScopeService.pickableDictTypes(this.getUserId(), roleId));
	}

	@ApiOperation("枚举类型字典选项（角色扩展维取值）")
	@GetMapping("dict/{dictCode}/options")
	public Result<List<SelectOption>> dictOptions(
			@ApiParam("字典类型编码") @PathVariable String dictCode) {
		return Result.ok(this.dictService.select(dictCode));
	}

	@ApiOperation("树形字典选项（TERM_CLASS / k_terms）")
	@GetMapping("dict/{dictCode}/tree-options")
	public Result<List<TreeSelectOption>> dictTreeOptions(
			@ApiParam("字典类型编码") @PathVariable String dictCode) {
		return Result.ok(this.dictService.selectTree(dictCode));
	}

	@ApiOperation("业务字段 ValueSource 选项")
	@GetMapping("dimensions/{dimensionCode}/options")
	public Result<List<SelectOption>> dimensionOptions(
			@ApiParam("业务字段编码") @PathVariable String dimensionCode) {
		return Result.ok(this.optionsService.resolveOptions(dimensionCode));
	}

	@ApiOperation("行政区划级联子节点")
	@GetMapping("regions/children")
	public Result<List<RegionCascadeNode>> regionChildren(
			@ApiParam("父节点 id，省级传 0") @RequestParam(required = false) Long parentId,
			@ApiParam("授权粒度：1省 2市 3区县") @RequestParam(defaultValue = "3") int selectLevel) {
		return Result.ok(this.regionCascadeService.listChildren(parentId, selectLevel));
	}

	@ApiOperation("行政区划按名称搜索")
	@GetMapping("regions/search")
	public Result<List<List<RegionCascadeNode>>> regionSearch(
			@ApiParam("名称关键字") @RequestParam String keyword,
			@ApiParam("授权粒度：1省 2市 3区县") @RequestParam(defaultValue = "3") int selectLevel) {
		return Result.ok(this.regionCascadeService.search(keyword, selectLevel));
	}

	@ApiOperation("行政区划码反查级联路径")
	@GetMapping("regions/paths")
	public Result<List<List<RegionCascadeNode>>> regionPaths(
			@ApiParam("国标码，逗号分隔") @RequestParam String codes,
			@ApiParam("授权粒度：1省 2市 3区县") @RequestParam(defaultValue = "3") int selectLevel) {
		if (!StringUtils.hasText(codes)) {
			return Result.ok(Collections.emptyList());
		}
		List<String> codeList = Arrays.stream(codes.split(","))
				.map(String::trim)
				.filter(StringUtils::hasText)
				.collect(Collectors.toList());
		return Result.ok(this.regionCascadeService.resolvePaths(codeList, selectLevel));
	}
}
