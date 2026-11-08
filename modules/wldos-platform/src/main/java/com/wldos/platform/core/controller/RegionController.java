/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.ArrayList;
import java.util.List;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.vo.RegionSearchPathVO;
import io.github.wldos.platform.support.region.vo.City;
import com.wldos.platform.core.entity.WoRegion;
import com.wldos.platform.core.service.RegionService;
import com.wldos.platform.core.vo.Prov;

import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 行政区划。
 *
 * @author Yuanxi Universe
 * @date 2021/6/7
 * @version 1.0
 */
@Api(tags = "行政区划")
@RequestMapping("region")
@RestController
public class RegionController extends EntityController<RegionService, WoRegion> {

	@ApiOperation(value = "省份列表", notes = "查询所有省份")
	@GetMapping("prov")
	public List<Prov> queryProv() {

		return this.service.queryProvince();
	}

	@ApiOperation(value = "按级别列出区划", notes = "1=省 2=市 3=区县；供表头多选筛选加载选项")
	@GetMapping("by-level/{level}")
	public List<Prov> listByLevel(@ApiParam(value = "级别：1/2/3", required = true) @PathVariable String level) {
		return new ArrayList<>(this.service.listByLevel(level));
	}

	/**
	 * 根据省分取所有地市
	 *
	 * @param province 省分id
	 */
	@ApiOperation(value = "城市列表", notes = "根据省份ID查询所有地市")
	@GetMapping(value = { "city/{province}", "city/" })
	public List<City> queryCity(@ApiParam(value = "省份ID") @PathVariable(required = false) Long province) {

		if (null == province)
			return new ArrayList<>();

		return new ArrayList<>(this.service.queryCityByProvId(province));
	}

	@ApiOperation(value = "区县列表", notes = "根据地级市 ID 查询区县")
	@GetMapping("county/{cityId}")
	public List<Prov> queryCounty(@ApiParam(value = "地级市 ID", required = true) @PathVariable Long cityId) {
		if (cityId == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(this.service.queryCountyByCityId(cityId));
	}

	@ApiOperation(value = "区域信息", notes = "根据城市ID查询区域信息")
	@GetMapping("info/{cityId}")
	public City regionInfo(@ApiParam(value = "城市ID", required = true) @PathVariable Long cityId) {

		return this.service.queryRegionInfo(cityId);
	}

	@ApiOperation(value = "行政区划搜索", notes = "按名称模糊搜索省/市/区县，返回完整路径")
	@GetMapping("search")
	public List<RegionSearchPathVO> search(
			@ApiParam(value = "搜索关键字", required = true) @RequestParam String keyword,
			@ApiParam(value = "最大返回条数，默认 30") @RequestParam(required = false) Integer limit) {
		return new ArrayList<>(this.service.searchPaths(keyword, limit));
	}
}
