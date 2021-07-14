/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.controller;

import java.util.List;

import com.wldos.support.controller.RepoController;
import com.wldos.system.entity.WoRegion;
import com.wldos.system.service.RegionService;
import com.wldos.system.vo.City;
import com.wldos.system.vo.Prov;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行政区划。
 *
 * @Title RegionController
 * @Package com.wldos.system.controller
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/6/7
 * @Version 1.0
 */
@RequestMapping("region")
@RestController
public class RegionController extends RepoController<RegionService, WoRegion> {

	@GetMapping("prov")
	public String queryProv() {
		List<Prov> provList = this.service.queryProvince();

		return this.resJson.ok(provList);
	}

	@GetMapping("city/{province}")
	public String queryCity(@PathVariable Long province) {
		List<City> cityList = this.service.queryCityByProvId(province);

		return this.resJson.ok(cityList);
	}

	@GetMapping("info/{cityId}")
	public String regionInfo(@PathVariable Long cityId) {
		City city = this.service.queryRegionInfo(cityId);

		return this.resJson.ok(city);
	}
}
