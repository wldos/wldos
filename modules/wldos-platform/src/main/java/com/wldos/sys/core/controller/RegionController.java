/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.sys.core.controller;

import java.util.ArrayList;
import java.util.List;

import com.wldos.framework.controller.RepoController;
import com.wldos.support.region.vo.City;
import com.wldos.sys.core.entity.WoRegion;
import com.wldos.sys.core.service.RegionService;
import com.wldos.sys.core.vo.Prov;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行政区划。
 *
 * @author 元悉宇宙
 * @date 2021/6/7
 * @version 1.0
 */
@RequestMapping("region")
@RestController
public class RegionController extends RepoController<RegionService, WoRegion> {

	@GetMapping("prov")
	public List<Prov> queryProv() {

		return this.service.queryProvince();
	}

	/**
	 * 根据省分取所有地市
	 *
	 * @param province 省分id
	 */
	@GetMapping(value = { "city/{province}", "city/" })
	public List<City> queryCity(@PathVariable(required = false) Long province) {

		if (null == province)
			return new ArrayList<>();

		return new ArrayList<>(this.service.queryCityByProvId(province));
	}

	@GetMapping("info/{cityId}")
	public City regionInfo(@PathVariable Long cityId) {

		return this.service.queryRegionInfo(cityId);
	}
}
