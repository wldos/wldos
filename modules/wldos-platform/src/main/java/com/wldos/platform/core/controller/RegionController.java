/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.ArrayList;
import java.util.List;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.support.region.vo.City;
import com.wldos.platform.core.entity.WoRegion;
import com.wldos.platform.core.service.RegionService;
import com.wldos.platform.core.vo.Prov;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 行政区划。
 *
 * @author 元悉宇宙
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

	@ApiOperation(value = "区域信息", notes = "根据城市ID查询区域信息")
	@GetMapping("info/{cityId}")
	public City regionInfo(@ApiParam(value = "城市ID", required = true) @PathVariable Long cityId) {

		return this.service.queryRegionInfo(cityId);
	}
}
