/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;

import com.wldos.support.service.BaseService;
import com.wldos.system.core.entity.WoRegion;
import com.wldos.system.core.repo.RegionRepo;
import com.wldos.system.enums.RegionLevelEnum;
import com.wldos.system.vo.City;
import com.wldos.system.vo.Prov;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行政区划管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RegionService extends BaseService<RegionRepo, WoRegion, Long> {

	public List<Prov> queryProvince() {
		return this.entityRepo.queryByLevel(RegionLevelEnum.PROV.toString());
	}

	public List<City> queryCityByProvId(Long provId) {

		return this.entityRepo.queryCity(provId, RegionLevelEnum.CITY.toString());
	}

	public City queryRegionInfo(Long cityId) {
		return this.entityRepo.queryCityById(cityId, RegionLevelEnum.CITY.toString());
	}

	public City queryRegionInfoByCode(String cityCode) {
		return this.entityRepo.queryCityByCode(cityCode, RegionLevelEnum.CITY.toString());
	}
}
