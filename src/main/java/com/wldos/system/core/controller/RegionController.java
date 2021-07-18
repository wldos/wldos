/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.List;

import com.wldos.support.controller.RepoController;
import com.wldos.system.core.entity.WoRegion;
import com.wldos.system.core.service.RegionService;
import com.wldos.system.vo.City;
import com.wldos.system.vo.Prov;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行政区划。
 *
 * @author 树悉猿
 * @date 2021/6/7
 * @version 1.0
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
